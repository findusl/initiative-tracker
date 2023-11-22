package de.lehrbaum.initiativetracker.ui.host

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
	GeneralDialog(
		onDismissRequest = onDismiss
	) {
		Column(
			modifier = Modifier.padding(16.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			Text("Apply ${options.damage} Damage to ${options.targetName}", style = MaterialTheme.typography.h6)
			Text("Probable source: ${options.sourceName}", style = MaterialTheme.typography.subtitle2)
			DamageDecision.values().forEach { option ->
				Button(
					onClick = { onDamageApplied(option) },
					modifier = Modifier.fillMaxWidth()
				) {
					Text(option.getLabel())
				}
			}
		}
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
