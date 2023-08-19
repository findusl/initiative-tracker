package de.lehrbaum.initiativetracker.ui.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun PreviewCombatantListElement() {
	val combatant = mockCombatant()
	CombatantListElement(combatant = combatant)
}
