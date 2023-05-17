package de.lehrbaum.initiativetracker.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import de.lehrbaum.initiativetracker.ui.Constants
import de.lehrbaum.initiativetracker.ui.GeneralDialog
import kotlin.math.roundToInt

@Composable
fun DamageCombatantDialog(onSubmit: (Int) -> Unit, onCancel: () -> Unit) {
	GeneralDialog(onDismissRequest = { onCancel() }) {
		Surface(
			shape = RoundedCornerShape(16.dp),
			color = Color.White
		) {
			DamageCombatantDialogContent(onSubmit, onCancel)
		}
	}
}

@Composable
fun DamageCombatantDialogContent(onSubmit: (Int) -> Unit, onCancel: () -> Unit) {
	var sliderValue by remember { mutableStateOf(1.0f) }
	val sliderValueString by remember { derivedStateOf { sliderValue.roundToInt().toString() } }
	var textInputValue by remember(sliderValueString) { mutableStateOf(sliderValueString) }
	val textInputError by remember { derivedStateOf { textInputValue.toIntOrNull() == null } }

	Column(modifier = Modifier.padding(Constants.defaultPadding)) {
		Row(
			horizontalArrangement = Arrangement.Center,
			verticalAlignment = Alignment.CenterVertically
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
				singleLine = true,
				modifier = Modifier.weight(1.0f)
			)
			IconButton(
				onClick = { sliderValue++ }
			) {
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
		OkCancelButtonRow(mutableStateOf(true), onCancel, onSubmit = { onSubmit(sliderValue.roundToInt()) })
	}
}
