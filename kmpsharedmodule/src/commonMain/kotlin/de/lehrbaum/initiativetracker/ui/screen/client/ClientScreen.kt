package de.lehrbaum.initiativetracker.ui.screen.client

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import de.lehrbaum.initiativetracker.bl.ClientCombatState
import de.lehrbaum.initiativetracker.ui.model.client.ClientCombatModel
import de.lehrbaum.initiativetracker.ui.model.shared.toCombatantViewModel
import de.lehrbaum.initiativetracker.ui.screen.components.BurgerMenuButtonForDrawer
import de.lehrbaum.initiativetracker.ui.screen.components.CombatantList
import de.lehrbaum.initiativetracker.ui.screen.components.bindSnackbarState

@Composable
@ExperimentalFoundationApi
@ExperimentalMaterialApi
fun ClientScreen(drawerState: DrawerState, clientCombatModel: ClientCombatModel) {
	val connectionStateState = clientCombatModel.combatState.collectAsState(ClientCombatState.Connecting)
	val scaffoldState = rememberScaffoldState()
	scaffoldState.snackbarHostState.bindSnackbarState(clientCombatModel.snackbarState)

	Scaffold(
		scaffoldState = scaffoldState,
		topBar = { TopBar(drawerState, clientCombatModel) },
	) {
		Content(connectionStateState, clientCombatModel)
	}
}

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
private fun Content(
	connectionStateState: State<ClientCombatState>,

	clientCombatModel: ClientCombatModel
) {
	val connectionState = connectionStateState.value
	when (connectionState) {
		is ClientCombatState.Connected -> {
			CombatantList(
				connectionState.combatants.mapIndexed { index, combatant ->
					combatant.toCombatantViewModel(index == connectionState.activeCombatantIndex)
				},
				clientCombatModel::onCombatantClicked,
				clientCombatModel::onCombatantLongClicked,
			)
		}

		ClientCombatState.Connecting -> Text("Connecting")
		is ClientCombatState.Disconnected -> Text("Disconnected. Reason: ${connectionState.reason}")
	}
}

@Composable
private fun TopBar(
	drawerState: DrawerState,
	clientCombatModel: ClientCombatModel
) {
	TopAppBar(
		title = {
			Text("Combat ${clientCombatModel.sessionId}", color = MaterialTheme.colors.onPrimary)
		},
		navigationIcon = { BurgerMenuButtonForDrawer(drawerState) },
		actions = {
			IconButton(onClick = clientCombatModel::leaveCombat) {
				Icon(Icons.Default.Close, contentDescription = "Leave Session")
			}
		},
		backgroundColor = MaterialTheme.colors.primarySurface,
	)
}
