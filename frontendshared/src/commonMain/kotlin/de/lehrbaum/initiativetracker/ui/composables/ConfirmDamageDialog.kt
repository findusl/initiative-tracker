package de.lehrbaum.initiativetracker.ui.composables

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
import de.lehrbaum.initiativetracker.ui.GeneralDialog
import de.lehrbaum.initiativetracker.ui.shared.CombatantViewModel

enum class DamageOption {
	FULL, HALF, DOUBLE, NONE
}

data class ConfirmDamageOptions(
	val damage: Int,
	val target: CombatantViewModel,
	val sourceName: String?,
) {
	val targetName
		get() = target.name
}

@Composable
fun ConfirmDamageDialog(
	options: ConfirmDamageOptions,
	onDamageApplied: (DamageOption) -> Unit,
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
			DamageOption.values().forEach { option ->
				Button(
					onClick = { onDamageApplied(option) },
					modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
				) {
					Text(option.getLabel())
				}
			}
		}
	}
}

private fun DamageOption.getLabel(): String {
	return when (this) {
		DamageOption.FULL -> "Full"
		DamageOption.HALF -> "Half"
		DamageOption.DOUBLE -> "Double"
		DamageOption.NONE -> "None"
	}
}
