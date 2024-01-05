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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import de.lehrbaum.initiativetracker.ui.Constants
import de.lehrbaum.initiativetracker.ui.composables.CoroutineWrapper
import de.lehrbaum.initiativetracker.ui.composables.DiceRollingTextField
import de.lehrbaum.initiativetracker.ui.composables.OkCancelButtonRow
import de.lehrbaum.initiativetracker.ui.composables.rememberCoroutineScope
import de.lehrbaum.initiativetracker.ui.keyevents.defaultFocussed
import kotlinx.coroutines.launch

@Composable
fun DamageCombatantDialog(viewModel: DamageCombatantViewModel) {
	val coroutineScope = rememberCoroutineScope(viewModel)
	Dialog(onDismissRequest = viewModel.onCancel) {
		Surface(
			shape = RoundedCornerShape(16.dp),
			color = Color.White,
			modifier = Modifier
				.onKeyEvent { keyEvent ->
					if (viewModel.textIsValidNumber.value && keyEvent.key == Key.Enter && keyEvent.type == KeyEventType.KeyUp) {
						coroutineScope.launch { viewModel.onSubmit() }
						true
					} else false
				}
		) {
			DamageCombatantDialogContent(viewModel, coroutineScope)
		}
	}
}

@Composable // not skippable because of coroutineScope but parent is trivial and skippable
private fun DamageCombatantDialogContent(viewModel: DamageCombatantViewModel, coroutineScope: CoroutineWrapper) {

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
				modifier = Modifier
					.weight(1.0f)
					.defaultFocussed(viewModel),
				onInputValidChanged = { viewModel.textIsValidNumber.value = it }
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
