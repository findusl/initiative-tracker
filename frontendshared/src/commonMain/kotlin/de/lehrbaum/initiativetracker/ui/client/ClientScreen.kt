package de.lehrbaum.initiativetracker.ui.client

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DrawerState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import de.lehrbaum.initiativetracker.bl.ClientCombatState
import de.lehrbaum.initiativetracker.ui.character.CharacterChooserScreen
import de.lehrbaum.initiativetracker.ui.composables.BurgerMenuButtonForDrawer
import de.lehrbaum.initiativetracker.ui.composables.CombatantList
import de.lehrbaum.initiativetracker.ui.composables.CombatantListViewModel
import de.lehrbaum.initiativetracker.ui.composables.KeepScreenOn
import de.lehrbaum.initiativetracker.ui.composables.ResettableState
import de.lehrbaum.initiativetracker.ui.composables.bindSnackbarState
import de.lehrbaum.initiativetracker.ui.composables.collectAsStateResettable
import de.lehrbaum.initiativetracker.ui.composables.rememberCoroutineScope
import de.lehrbaum.initiativetracker.ui.composables.rememberScaffoldState
import de.lehrbaum.initiativetracker.ui.damage.DamageCombatantDialog
import de.lehrbaum.initiativetracker.ui.edit.EditCombatantScreen
import de.lehrbaum.initiativetracker.ui.icons.FastForward
import de.lehrbaum.initiativetracker.ui.shared.ListDetailLayout
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

@Composable
@ExperimentalFoundationApi
@ExperimentalMaterialApi
fun ClientScreen(drawerState: DrawerState, clientCombatViewModel: ClientCombatViewModel) {
	val clientCombatViewModelState = remember { mutableStateOf(clientCombatViewModel) }
	clientCombatViewModelState.value = clientCombatViewModel
	val connectionStateState = clientCombatViewModel.combatState.collectAsStateResettable(ClientCombatState.Connecting)
	val coroutineScope = rememberCoroutineScope(clientCombatViewModel.combatLink)
	KeepScreenOn()

	ListDetailLayout(
		list = {
			val scaffoldState = rememberScaffoldState(clientCombatViewModel, drawerState)
			scaffoldState.snackbarHostState.bindSnackbarState(clientCombatViewModelState.value.snackbarState)

			Scaffold(
				scaffoldState = scaffoldState,
				floatingActionButton = {
					(connectionStateState.value as? ClientCombatState.Connected)?.let {
						NextCombatantButton(clientCombatViewModelState.value, it)
					}
				},
				topBar = {
					TopBar(drawerState, clientCombatViewModelState.value, onAddCharacter = {
						coroutineScope.launch { clientCombatViewModel.chooseCharacterToAdd() }
					})
				},
			) {
				Content(clientCombatViewModelState.value, connectionStateState)
			}

			if (connectionStateState.value is ClientCombatState.Connected) {
				clientCombatViewModelState.value.run {
					damageCombatantViewModel?.let {
						DamageCombatantDialog(it)
					}
				}
			}
		},
		detail = if (connectionStateState.value is ClientCombatState.Connected) {
			clientCombatViewModel.characterChooserViewModel?.let { { CharacterChooserScreen(it) } }
				?: clientCombatViewModel.editCombatantViewModel?.let { { EditCombatantScreen(it) } }
		} else {
			null
		},
	)
}

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
private fun Content(clientCombatViewModel: ClientCombatViewModel, connectionStateState: ResettableState<ClientCombatState>) {
	val connectionState = connectionStateState.value
	when (connectionState) {
		is ClientCombatState.Connected -> {
			val combatantList = connectionState.combatants
				.mapIndexed { index, combatant ->
					CombatantListViewModel(
						combatant,
						active = index == connectionState.activeCombatantIndex,
						isOwned = combatant.ownerId == clientCombatViewModel.ownerId,
					)
				}.toImmutableList()
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
				Button({ connectionStateState.reset() }) {
					Text("Restart Connection")
				}
			}
		}
	}
}

@Composable
private fun TopBar(
	drawerState: DrawerState,
	clientCombatViewModel: ClientCombatViewModel,
	onAddCharacter: () -> Unit,
) {
	TopAppBar(
		title = {
			Text(clientCombatViewModel.title, color = MaterialTheme.colors.onPrimary)
		},
		navigationIcon = { BurgerMenuButtonForDrawer(drawerState) },
		actions = {
			IconButton(onClick = onAddCharacter) {
				Icon(Icons.Default.Add, contentDescription = "Choose Character to add")
			}
			IconButton(onClick = clientCombatViewModel::leaveCombat) {
				Icon(Icons.Default.Close, contentDescription = "Leave Session")
			}
		},
		backgroundColor = MaterialTheme.colors.primarySurface,
	)
}

@Composable
private fun NextCombatantButton(clientCombatViewModel: ClientCombatViewModel, connectionStateState: ClientCombatState.Connected) {
	val coroutineScope = rememberCoroutineScope(connectionStateState)
	if (connectionStateState.activeCombatantIndex >= 0 &&
		connectionStateState.combatants[connectionStateState.activeCombatantIndex].ownerId == clientCombatViewModel.ownerId
	) {
		var finishTurnLoading by remember(connectionStateState) { mutableStateOf(false) }
		FloatingActionButton(onClick = {
			coroutineScope.launch {
				finishTurnLoading = true
				clientCombatViewModel.finishTurn(connectionStateState.activeCombatantIndex)
				finishTurnLoading = false
			}
		}) {
			if (finishTurnLoading) {
				CircularProgressIndicator(
					color = MaterialTheme.colors.onPrimary,
					strokeWidth = 3.dp,
				)
			} else {
				Icon(Icons.Default.FastForward, contentDescription = "Finish Turn")
			}
		}
	}
}
