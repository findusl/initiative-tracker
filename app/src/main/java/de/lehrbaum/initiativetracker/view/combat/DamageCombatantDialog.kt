package de.lehrbaum.initiativetracker.view.combat.host

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Slider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import kotlin.math.roundToInt

@Composable
fun DamageCombatantDialog() {
	var sliderValue by remember { mutableStateOf(1.0f) }
	val sliderValueString by remember { derivedStateOf { sliderValue.roundToInt().toString() } }
	var textInputValue by remember(sliderValueString) { mutableStateOf(sliderValueString) }
	val textInputError by remember { derivedStateOf { textInputValue.toIntOrNull() == null } }

	Column(modifier = Modifier.fillMaxWidth()) {
		Row(
			horizontalArrangement = Arrangement.Center,
			verticalAlignment = Alignment.CenterVertically,
			modifier = Modifier.fillMaxWidth()
		) {
			IconButton(onClick = { sliderValue-- }) {
				Icon(
					imageVector = Icons.Filled.ArrowBack,
					contentDescription = "Decrement",
					tint = Color.Black
				)
			}
			// This text should be centered
			OutlinedTextField(
				value = textInputValue,
				onValueChange =
				{ currentInput ->
					textInputValue = currentInput
					currentInput.toIntOrNull()?.let { sliderValue = it.toFloat() }
				},
				keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
				isError = textInputError,
				singleLine = true
			)
			IconButton(onClick = { sliderValue++ }) {
				Icon(
					imageVector = Icons.Filled.ArrowForward,
					contentDescription = "Increment",
					tint = Color.Black
				)
			}
		}
		Slider(
			value = sliderValue,
			valueRange = 0f..100f,
			onValueChange = { sliderValue = it }
		)
		// Maybe remove second slider, doesn't seem so necessary
		/*Slider(
			value = sliderValue % 10,
			valueRange = 0f..10f,
			steps = 9,
			onValueChange = { sliderValue = (sliderValue / 10).toInt() * 10f + it }
		)*/

	}
}


@Preview(device = Devices.NEXUS_5, showBackground = true, showSystemUi = true)
@Composable
fun EditCombatantDialogPreview() {
	DamageCombatantDialog()
}
