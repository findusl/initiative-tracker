package de.lehrbaum.initiativetracker.ui.screen

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import de.lehrbaum.initiativetracker.bl.ClientCombatState
import de.lehrbaum.initiativetracker.bl.CombatantModel
import de.lehrbaum.initiativetracker.ui.model.CombatantViewModel
import de.lehrbaum.initiativetracker.ui.model.client.ClientCombatModel
import de.lehrbaum.initiativetracker.ui.model.toCombatantViewModel
import de.lehrbaum.initiativetracker.ui.screen.components.BurgerMenuButtonForDrawer
import de.lehrbaum.initiativetracker.ui.screen.components.CombatantListElement

@Composable
fun ClientScreen(drawerState: DrawerState, clientCombatModel: ClientCombatModel) {
	val state by clientCombatModel.combatState.collectAsState(ClientCombatState.Connecting)
	val combatJoined = state is ClientCombatState.Connected
	Scaffold(
		topBar = { TopBar(drawerState, clientCombatModel.combatId, clientCombatModel::leaveCombat) }
	) {
		Content(state, clientCombatModel.combatId)
	}
}

@Composable
private fun TopBar(drawerState: DrawerState, combatId: Int, leaveCombat: () -> Unit) {
	TopAppBar(
		title = { Text("Combat $combatId", color = MaterialTheme.colors.onPrimary) },
		navigationIcon = { BurgerMenuButtonForDrawer(drawerState) },
		actions = {
			IconButton(onClick = leaveCombat) {
				Icon(Icons.Default.Close, contentDescription = "Leave combat")
			}
		}
	)
}

@Composable
private fun Content(state: ClientCombatState, combatId: Int) {
	when (state) {
		is ClientCombatState.Connected -> CombatantList(state.activeCombatantIndex, state.combatants)
		ClientCombatState.Connecting -> Text("Connecting")
		is ClientCombatState.Disconnected -> Text("Disconnected. ${state.reason}")
	}
}

@Composable
private fun CombatantList(activeCombatantIndex: Int, combatantModels: List<CombatantModel>) {
	// not such a good style to do this mapping here, but damn it, I don't want to copy the state variables
	val combatants = combatantModels.mapIndexed { index, model ->
		model.toCombatantViewModel(index == activeCombatantIndex)
	}
	val listState = rememberLazyListState()

	// Animate scrolling to the active character's position
	LaunchedEffect(activeCombatantIndex) {
		if (activeCombatantIndex != -1) {
			listState.animateScrollToItem(activeCombatantIndex, scrollOffset = -200)
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
