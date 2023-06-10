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
fun ClientScreen(drawerState: DrawerState, clientCombatViewModel: ClientCombatViewModel) {
	val clientCombatModelState = remember { mutableStateOf(clientCombatViewModel) }
	clientCombatModelState.value = clientCombatViewModel
	val connectionStateState = clientCombatViewModel.combatState.collectAsState(ClientCombatState.Connecting)
	val coroutineScope = rememberCoroutineScope(clientCombatViewModel.sessionId)

	ListDetailLayout(
		list = {
			val scaffoldState = rememberScaffoldState()
			scaffoldState.snackbarHostState.bindSnackbarState(clientCombatViewModel.snackbarState)

			Scaffold(
				scaffoldState = scaffoldState,
				topBar = { TopBar(drawerState, clientCombatModelState.value, coroutineScope) },
			) {
				Content(connectionStateState, clientCombatViewModel)
			}
		},
		detail = if (connectionStateState.value is ClientCombatState.Connected) {
			clientCombatViewModel.characterChooserViewModel?.let { { CharacterChooserScreen(it) } }
				?: clientCombatViewModel.editCombatantViewModel?.let { { EditCombatantScreen(it) } }

		} else null
	)
}

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
private fun Content(
	connectionStateState: State<ClientCombatState>,
	clientCombatViewModel: ClientCombatViewModel
) {
	val connectionState = connectionStateState.value
	when (connectionState) {
		is ClientCombatState.Connected -> {
			val combatantList = connectionState.combatants.mapIndexed { index, combatant ->
				val viewModel = combatant.toCombatantViewModel(active = index == connectionState.activeCombatantIndex)
				if (viewModel.isHidden && clientCombatViewModel.ownerId != viewModel.ownerId) {
					viewModel.copy(name = "<Hidden>")
				} else {
					viewModel
				}
			}
			CombatantList(
				combatantList,
				clientCombatViewModel::onCombatantClicked,
				clientCombatViewModel::onCombatantLongClicked,
			)
		}

		ClientCombatState.Connecting -> Text("Connecting")
		is ClientCombatState.Disconnected -> Text("Disconnected. Reason: ${connectionState.reason}")
	}
}

@Composable
private fun TopBar(
	drawerState: DrawerState,
	clientCombatViewModel: ClientCombatViewModel,
	coroutineScope: CoroutineScope,
) {
	TopAppBar(
		title = {
			Text("Joined ${clientCombatViewModel.sessionId}", color = MaterialTheme.colors.onPrimary)
		},
		navigationIcon = { BurgerMenuButtonForDrawer(drawerState) },
		actions = {
			IconButton(onClick = {
				coroutineScope.launch { clientCombatViewModel.chooseCharacterToAdd() }
			}) {
				Icon(Icons.Default.Add, contentDescription = "Choose Character to add")
			}
			IconButton(onClick = clientCombatViewModel::leaveCombat) {
				Icon(Icons.Default.Close, contentDescription = "Leave Session")
			}
		},
		backgroundColor = MaterialTheme.colors.primarySurface,
	)
}
