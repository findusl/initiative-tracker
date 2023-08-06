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

enum class DamageOption {
	FULL, HALF, DOUBLE, NONE
}

@Composable
fun ConfirmDamageDialog(
	playerDamage: Int,
	combatantName: String,
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
			Text("Apply Damage", style = MaterialTheme.typography.h6)
			Text("Damage $combatantName: $playerDamage")
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
