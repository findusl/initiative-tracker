package de.lehrbaum.initiativetracker.view.combat.client

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.Path
import androidx.compose.ui.tooling.preview.Preview
import de.lehrbaum.initiativetracker.ui.model.CombatantViewModel
import de.lehrbaum.initiativetracker.ui.screen.Constants
import de.lehrbaum.initiativetracker.ui.screen.components.CombatantListElement
import kotlinx.coroutines.flow.StateFlow

@Stable
interface ClientCombatViewModel {
	val combatants: StateFlow<List<CombatantViewModel>>
}

@Composable
fun CombatClientScreen(clientCombatViewModel: ClientCombatViewModel) {
	CombatantList(clientCombatViewModel)
}

@Composable
private fun CombatantList(clientCombatViewModel: ClientCombatViewModel) {
	val combatants by clientCombatViewModel.combatants.collectAsState()
	val listState = rememberLazyListState()

	// Find the index of the active character
	val activeCharacterIndex = combatants.indexOfFirst { it.active }

	// Animate scrolling to the active character's position
	LaunchedEffect(activeCharacterIndex) {
		if (activeCharacterIndex != -1) {
			listState.animateScrollToItem(activeCharacterIndex, scrollOffset = -200)
		}
	}
	LazyColumn(state = listState) {
		val itemModifier = Modifier
			.padding(Constants.defaultPadding)
			.fillMaxWidth()
		items(combatants, key = CombatantViewModel::id) { combatant ->
			CombatantListElement(combatant, itemModifier)
		}
	}
}
