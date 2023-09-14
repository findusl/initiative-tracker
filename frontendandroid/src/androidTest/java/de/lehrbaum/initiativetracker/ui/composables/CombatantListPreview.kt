@file:Suppress("TestFunctionName")

package de.lehrbaum.initiativetracker.ui.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Preview(name = "ListPreview", device = "spec:width=411dp,height=800dp", showSystemUi = true, showBackground = true)
@Composable
fun PreviewCombatantList() {
	val combatants = (0..10).map { mockCombatant(mockIndex = it) }
	CombatantList(
		combatants = combatants,
		onCombatantClicked = {},
		onCombatantLongClicked = {},
		isHost = true
	)
}
