package de.lehrbaum.initiativetracker.ui.damage

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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import de.lehrbaum.initiativetracker.ui.Constants
import de.lehrbaum.initiativetracker.ui.GeneralDialog
import de.lehrbaum.initiativetracker.ui.composables.DiceRollingTextField
import de.lehrbaum.initiativetracker.ui.composables.OkCancelButtonRow
import kotlinx.coroutines.launch

@Composable
fun DamageCombatantDialog(viewModel: DamageCombatantViewModel) {
	GeneralDialog(onDismissRequest = viewModel.onCancel) {
		Surface(
			shape = RoundedCornerShape(16.dp),
			color = Color.White
		) {
			DamageCombatantDialogContent(viewModel)
		}
	}
}

@Composable
fun DamageCombatantDialogContent(viewModel: DamageCombatantViewModel) {
	val coroutineScope = rememberCoroutineScope()

	Column(modifier = Modifier.padding(Constants.defaultPadding)) {
		Text("Damage ${viewModel.target}")
		Spacer(Modifier.height(Constants.defaultPadding))
		Row(
			horizontalArrangement = Arrangement.Center,
			verticalAlignment = Alignment.CenterVertically
		) {
			IconButton(onClick = { viewModel.sliderValue-- }) {
				Icon(
					imageVector = Icons.Filled.ArrowBack,
					contentDescription = "Decrement",
					tint = Color.Black
				)
			}
			// This text should be centered
			DiceRollingTextField(
				initialNumber = viewModel.sliderValueInt,
				onNumberChanged = {
					if (it != viewModel.sliderValueInt)
						viewModel.sliderValue = it.toFloat()
				},
				modifier = Modifier.weight(1.0f),
				textIsValidNumber = viewModel.textIsValidNumber,
			)
			IconButton(
				onClick = { viewModel.sliderValue++ }
			) {
				Icon(
					imageVector = Icons.Filled.ArrowForward,
					contentDescription = "Increment",
					tint = Color.Black
				)
			}
		}
		Slider(
			value = viewModel.sliderValue,
			valueRange = 0f..100f,
			onValueChange = { viewModel.sliderValue = it }
		)
		OkCancelButtonRow(
			viewModel.textIsValidNumber.value,
			viewModel.onCancel,
			onSubmit = { coroutineScope.launch { viewModel.onSubmit() } },
			viewModel.isSubmitting
		)
	}
}
