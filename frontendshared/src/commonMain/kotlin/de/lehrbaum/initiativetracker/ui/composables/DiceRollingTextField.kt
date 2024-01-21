package de.lehrbaum.initiativetracker.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.KeyboardType
import de.lehrbaum.initiativetracker.bl.Dice
import kotlin.random.Random

/**
 * Works different from the normal text fields, as it updates its own value without the caller having to do that.
 * Simply to avoid duplicating the code to a caller, who is actually only interested in valid number results.
 *
 * @param onNumberChanged When the number in the text field changed, either because of Keyboard actions or
 * because a calculation was accepted
 */
@Composable
fun DiceRollingTextField(
	modifier: Modifier = Modifier,
	initialNumber: Int? = null,
	initialText: String = initialNumber?.toString() ?: "",
	label: String? = null,
	onNumberChanged: (Int) -> Unit,
	onInputValidChanged: (Boolean) -> Unit,
	placeholder: String? = null,
) {
	val textFieldContentState = remember(initialText) { mutableStateOf(initialText) }
	var textFieldContent by textFieldContentState
	LaunchedEffect(initialText) {
		val parsed = textFieldContent.toIntOrNull()?.also { onNumberChanged(it) }
		onInputValidChanged(parsed != null)
	}
	var isFocussed by remember { mutableStateOf(false) }
	var textFieldWidth by remember { mutableStateOf(0) }
	var textFieldHeight by remember { mutableStateOf(0) }
	val seed = remember { Random.nextLong() }
	val diceCalculationResult by remember(textFieldContentState) { derivedStateOf { Dice.calculateDiceFormula(textFieldContent, seed) } }
	val error by remember(textFieldContentState) { derivedStateOf { textFieldContent.toIntOrNull() == null && diceCalculationResult == null } }

	Column(modifier) {
		OutlinedTextField(
			value = textFieldContent,
			onValueChange = { newValue ->
				textFieldContent = newValue
				val parsed = newValue.toIntOrNull()
				parsed?.let(onNumberChanged)
				onInputValidChanged(parsed != null)
			},
			modifier = Modifier.fillMaxWidth()
				.onSizeChanged {
					textFieldWidth = it.width
					textFieldHeight = it.height
				}.onFocusChanged {
					isFocussed = it.isFocused
				},
			label = label?.let { { Text(it) } },
			isError = error && !isFocussed,
			keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, autoCorrect = false),
			singleLine = true,
			placeholder = placeholder?.let { { Text(it) } },
		)
		MyDropdownMenu(
			expanded = isFocussed && diceCalculationResult != null,
			focusable = false,
			modifier = Modifier
				.width(with(LocalDensity.current) { textFieldWidth.toDp() })
				.requiredHeightIn(max = with(LocalDensity.current) { (5 * textFieldHeight).toDp() }),
		) {
			diceCalculationResult?.let { result ->
				DropdownMenuItem(onClick = {
					textFieldContent = result.sum.toString()
					onNumberChanged(result.sum)
					onInputValidChanged(true)
				}) {
					Column {
						if (result.intermediateStep != result.sum.toString())
							Text("=${result.intermediateStep}")
						Text("=${result.sum}")
					}
				}
			}
		}
	}
}
