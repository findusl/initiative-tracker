package de.lehrbaum.initiativetracker.ui.client

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import de.lehrbaum.initiativetracker.bl.ClientCombatState
import de.lehrbaum.initiativetracker.ui.character.CharacterChooserScreen
import de.lehrbaum.initiativetracker.ui.composables.BurgerMenuButtonForDrawer
import de.lehrbaum.initiativetracker.ui.composables.CombatantList
import de.lehrbaum.initiativetracker.ui.composables.bindSnackbarState
import de.lehrbaum.initiativetracker.ui.composables.rememberCoroutineScope
import de.lehrbaum.initiativetracker.ui.edit.EditCombatantScreen
import de.lehrbaum.initiativetracker.ui.shared.ListDetailLayout
import de.lehrbaum.initiativetracker.ui.shared.toCombatantViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
@ExperimentalFoundationApi
@ExperimentalMaterialApi
fun ClientScreen(drawerState: DrawerState, clientCombatModel: ClientCombatModel) {
	val clientCombatModelState = remember { mutableStateOf(clientCombatModel) }
	clientCombatModelState.value = clientCombatModel
	val connectionStateState = clientCombatModel.combatState.collectAsState(ClientCombatState.Connecting)
	val coroutineScope = rememberCoroutineScope(clientCombatModel.sessionId)

	ListDetailLayout(
		list = {
			val scaffoldState = rememberScaffoldState()
			scaffoldState.snackbarHostState.bindSnackbarState(clientCombatModel.snackbarState)

			Scaffold(
				scaffoldState = scaffoldState,
				topBar = { TopBar(drawerState, clientCombatModelState.value, coroutineScope) },
			) {
				Content(connectionStateState, clientCombatModel)
			}
		},
		detail = if (connectionStateState.value is ClientCombatState.Connected) {
			clientCombatModel.characterChooserModel?.let { { CharacterChooserScreen(it) } }
				?: clientCombatModel.editCombatantModel?.let { { EditCombatantScreen(it) } }

		} else null
	)
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
					val viewModel = combatant.toCombatantViewModel(index == connectionState.activeCombatantIndex)
					if (viewModel.isHidden && clientCombatModel.ownerId != viewModel.ownerId) {
						viewModel.copy(name = "<Hidden>")
					} else {
						viewModel
					}
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
	clientCombatModel: ClientCombatModel,
	coroutineScope: CoroutineScope,
) {
	TopAppBar(
		title = {
			Text("Joined ${clientCombatModel.sessionId}", color = MaterialTheme.colors.onPrimary)
		},
		navigationIcon = { BurgerMenuButtonForDrawer(drawerState) },
		actions = {
			IconButton(onClick = {
				coroutineScope.launch { clientCombatModel.chooseCharacterToAdd() }
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
