package de.lehrbaum.initiativetracker.ui.host

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import de.lehrbaum.initiativetracker.bl.DamageDecision
import de.lehrbaum.initiativetracker.bl.DamageDecision.DOUBLE
import de.lehrbaum.initiativetracker.bl.DamageDecision.FULL
import de.lehrbaum.initiativetracker.bl.DamageDecision.HALF
import de.lehrbaum.initiativetracker.bl.DamageDecision.NONE
import de.lehrbaum.initiativetracker.ui.GeneralDialog

data class ConfirmDamageOptions(
	val damage: Int,
	val targetName: String,
	val sourceName: String?,
)

@Composable
fun ConfirmDamageDialog(
	options: ConfirmDamageOptions,
	onDamageApplied: (DamageDecision) -> Unit,
	onDismiss: () -> Unit
) {
	val focusRequester = remember(options) { FocusRequester() }
	GeneralDialog(
		onDismissRequest = onDismiss
	) {
		Column(
			modifier = Modifier
				.padding(16.dp)
				.focusRequester(focusRequester)
				.onKeyEvent {  keyEvent ->
					if (keyEvent.type != KeyEventType.KeyUp) false
					else {
						val index = when (keyEvent.key) {
							Key.One -> 0
							Key.Two -> 1
							Key.Three -> 2
							Key.Four -> 3
							else -> null
						}
						index?.let {
							onDamageApplied(DamageDecision.entries[it])
							true
						} ?: false
					}
				},
			verticalArrangement = Arrangement.spacedBy(8.dp),
		) {
			Text("Apply ${options.damage} Damage to ${options.targetName}", style = MaterialTheme.typography.h6)
			Text("Probable source: ${options.sourceName}", style = MaterialTheme.typography.subtitle2)
			DamageDecision.entries.forEach { option ->
				Button(
					onClick = { onDamageApplied(option) },
					modifier = Modifier.fillMaxWidth()
				) {
					Text(option.getLabel())
				}
			}
		}
	}

	LaunchedEffect(options) {
		focusRequester.requestFocus()
	}
}

private fun DamageDecision.getLabel(): String {
	return when (this) {
		FULL -> "Full"
		HALF -> "Half"
		DOUBLE -> "Double"
		NONE -> "None"
	}
}
