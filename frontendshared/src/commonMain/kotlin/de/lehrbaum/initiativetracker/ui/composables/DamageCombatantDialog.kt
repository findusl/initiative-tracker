package de.lehrbaum.initiativetracker.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import de.lehrbaum.initiativetracker.ui.Constants
import de.lehrbaum.initiativetracker.ui.GeneralDialog
import kotlin.math.roundToInt

@Composable
fun DamageCombatantDialog(target: String, onSubmit: suspend (Int) -> Unit, onCancel: () -> Unit) {
	GeneralDialog(onDismissRequest = { onCancel() }) {
		Surface(
			shape = RoundedCornerShape(16.dp),
			color = Color.White
		) {
			DamageCombatantDialogContent(target, onSubmit, onCancel)
		}
	}
}

@Composable
fun DamageCombatantDialogContent(target: String, onSubmit: suspend (Int) -> Unit, onCancel: () -> Unit) {
	var sliderValue by remember { mutableStateOf(1.0f) }
	val sliderValueInt by remember { derivedStateOf { sliderValue.roundToInt() } }
	val coroutineScope = rememberCoroutineScope()
	val textIsValidNumber = remember { mutableStateOf(false) }

	Column(modifier = Modifier.padding(Constants.defaultPadding)) {
		Text("Damage $target")
		Spacer(Modifier.height(Constants.defaultPadding))
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
			DiceRollingTextField(
				initialNumber = sliderValueInt,
				onNumberChanged = {
					if (it != sliderValueInt)
						sliderValue = it.toFloat()
				},
				modifier = Modifier.weight(1.0f),
				textIsValidNumber = textIsValidNumber,
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
		OkCancelButtonRow(
			textIsValidNumber.value,
			onCancel,
			onSubmitSuspend = { onSubmit(sliderValueInt) },
			coroutineScope
		)
	}
}
