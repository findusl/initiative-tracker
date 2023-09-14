package de.lehrbaum.initiativetracker.ui.client

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import de.lehrbaum.initiativetracker.bl.ClientCombatState
import de.lehrbaum.initiativetracker.ui.character.CharacterChooserScreen
import de.lehrbaum.initiativetracker.ui.composables.*
import de.lehrbaum.initiativetracker.ui.edit.EditCombatantScreen
import de.lehrbaum.initiativetracker.ui.shared.ListDetailLayout
import de.lehrbaum.initiativetracker.ui.shared.toCombatantViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@ExperimentalMaterial3Api
@Composable
@ExperimentalFoundationApi
fun ClientScreen(drawerState: DrawerState, clientCombatViewModel: ClientCombatViewModel) {
	val clientCombatViewModelState = remember { mutableStateOf(clientCombatViewModel) }
	clientCombatViewModelState.value = clientCombatViewModel
	val connectionStateState = clientCombatViewModel.combatState.collectAsStateResettable(ClientCombatState.Connecting)
	val coroutineScope = rememberCoroutineScope(clientCombatViewModel.sessionId)

	ListDetailLayout(
		list = {
			val snackbarHostState = remember { SnackbarHostState() }
			snackbarHostState.bindSnackbarState(clientCombatViewModelState.value.snackbarState)

			Scaffold(
				snackbarHost = { SnackbarHost(snackbarHostState) },
				topBar = { TopBar(drawerState, clientCombatViewModelState.value, coroutineScope) },
			) {
				Content(clientCombatViewModelState.value, connectionStateState)
			}

			if (connectionStateState.value is ClientCombatState.Connected) {
				clientCombatViewModelState.value.run {
					assignDamageCombatant?.let {
						DamageCombatantDialog(it.name, ::onDamageDialogSubmit, ::onDamageDialogCancel)
					}
				}
			}
		},
		detail = if (connectionStateState.value is ClientCombatState.Connected) {
			clientCombatViewModel.characterChooserViewModel?.let { { CharacterChooserScreen(it) } }
				?: clientCombatViewModel.editCombatantViewModel?.let { { EditCombatantScreen(it) } }
		} else null
	)
}

@ExperimentalMaterial3Api
@ExperimentalFoundationApi
@Composable
private fun Content(
	clientCombatViewModel: ClientCombatViewModel,
	connectionStateState: ResettableState<ClientCombatState>,
) {
	val connectionState = connectionStateState.value
	when (connectionState) {
		is ClientCombatState.Connected -> {
			val combatantList = connectionState.combatants.mapIndexed { index, combatant ->
				combatant.toCombatantViewModel(
					clientCombatViewModel.ownerId,
					active = index == connectionState.activeCombatantIndex
				)
			}
			CombatantList(
				combatantList,
				isHost = false,
				clientCombatViewModel::onCombatantClicked,
				clientCombatViewModel::onCombatantLongClicked,
			)
		}

		ClientCombatState.Connecting -> Text("Connecting")
		is ClientCombatState.Disconnected -> {
			Column {
				Text("Disconnected. Reason: ${connectionState.reason}")
				Button( { connectionStateState.reset() } ) {
					Text("Restart Connection")
				}
			}
		}
	}
}

@ExperimentalMaterial3Api
@Composable
private fun TopBar(
	drawerState: DrawerState,
	clientCombatViewModel: ClientCombatViewModel,
	coroutineScope: CoroutineScope,
) {
	TopAppBar(
		title = {
			Text("Joined ${clientCombatViewModel.sessionId}", /*color = MaterialTheme.colorScheme.onPrimaryContainer*/)
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
		//colors = topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
	)
}
