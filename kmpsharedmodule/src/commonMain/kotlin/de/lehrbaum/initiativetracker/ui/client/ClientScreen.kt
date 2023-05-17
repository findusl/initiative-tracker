package de.lehrbaum.initiativetracker.ui.client

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import de.lehrbaum.initiativetracker.bl.ClientCombatState
import de.lehrbaum.initiativetracker.ui.character.CharacterChooserDialog
import de.lehrbaum.initiativetracker.ui.composables.BurgerMenuButtonForDrawer
import de.lehrbaum.initiativetracker.ui.composables.CombatantList
import de.lehrbaum.initiativetracker.ui.composables.bindSnackbarState
import de.lehrbaum.initiativetracker.ui.composables.rememberCoroutineScope
import de.lehrbaum.initiativetracker.ui.shared.toCombatantViewModel
import kotlinx.coroutines.launch

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

	if (connectionStateState.value is ClientCombatState.Connected) {
		clientCombatModel.characterChooserModel?.let {
			CharacterChooserDialog(it)
		}
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
	val coroutineScope = rememberCoroutineScope(clientCombatModel)
	TopAppBar(
		title = {
			Text("Joined ${clientCombatModel.sessionId}", color = MaterialTheme.colors.onPrimary)
		},
		navigationIcon = { BurgerMenuButtonForDrawer(drawerState) },
		actions = {
			IconButton(onClick = {
				coroutineScope.launch {
					clientCombatModel.chooseCharacterToAdd()
				}
			}) {
				Icon(Icons.Default.Add, contentDescription = "Choose Character to add")
			}
			IconButton(onClick = clientCombatModel::leaveCombat) {
				Icon(Icons.Default.Close, contentDescription = "Leave Session")
			}
		},
		backgroundColor = MaterialTheme.colors.primarySurface,
	)
}
