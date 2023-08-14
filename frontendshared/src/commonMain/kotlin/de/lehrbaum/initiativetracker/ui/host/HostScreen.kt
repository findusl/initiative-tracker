package de.lehrbaum.initiativetracker.ui.host

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.*
import de.lehrbaum.initiativetracker.bl.HostConnectionState
import de.lehrbaum.initiativetracker.ui.composables.*
import de.lehrbaum.initiativetracker.ui.composables.DropdownMenu
import de.lehrbaum.initiativetracker.ui.edit.EditCombatantScreen
import de.lehrbaum.initiativetracker.ui.icons.FastForward
import de.lehrbaum.initiativetracker.ui.shared.ListDetailLayout
import kotlinx.coroutines.launch

@Composable
@ExperimentalMaterialApi
@ExperimentalFoundationApi
fun HostScreen(drawerState: DrawerState, hostCombatViewModel: HostCombatViewModel) {
	val hostCombatModelState = remember { mutableStateOf(hostCombatViewModel) }
	hostCombatModelState.value = hostCombatViewModel

	val connectionStateState = hostCombatViewModel.hostConnectionState.collectAsStateResettable(HostConnectionState.Connecting)

	ListDetailLayout(
		list = {
			val scaffoldState = rememberScaffoldState()
			scaffoldState.snackbarHostState.bindSnackbarState(hostCombatViewModel.snackbarState)

			Scaffold(
				scaffoldState = scaffoldState,
				topBar = { TopBar(drawerState, hostCombatModelState.value) },
				floatingActionButton = { NextCombatantButton(hostCombatModelState.value) },
			) {
				MainContent(hostCombatModelState.value, connectionStateState)
			}

			Dialogs(connectionStateState.value, hostCombatModelState.value)
		},
		detail = if (connectionStateState.value == HostConnectionState.Connected) {
			hostCombatViewModel.editCombatantViewModel.value?.let { { EditCombatantScreen(it) } }
		} else null
	)
}

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
private fun MainContent(
	hostCombatViewModel: HostCombatViewModel,
	connectionStateState: ResettableState<HostConnectionState>
) {
	hostCombatViewModel.run {
		val connectionState = connectionStateState.value

		when (connectionState) {
			HostConnectionState.Connected -> {
				CombatantList(
					combatants.collectAsState(emptyList()).value,
					::onCombatantClicked,
					::onCombatantLongClicked,
					::addNewCombatant,
					dismissToStartAction = {
						if (combatStarted && !it.disabled) swipeToDisable(::disableCombatant)
						else swipeToDelete(::deleteCombatant)
					},
					dismissToEndAction = {
						if (it.disabled) swipeToEnable(::enableCombatant)
						else if (combatStarted) swipeToJumpToTurn(::jumpToCombatant)
						else null
					}
				)
			}

			HostConnectionState.Connecting -> Text("Connecting")
			is HostConnectionState.Disconnected -> {
				Column {
					Text("Disconnected! Reason: ${connectionState.reason}")
					Button( { connectionStateState.reset() } ) {
						Text("Restart Connection")
					}
					Text("(Server may take up to 30 seconds to notice a missing host)")
				}
			}
		}
	}
}

@Composable
private fun Dialogs(
	connectionState: HostConnectionState,
	hostCombatViewModel: HostCombatViewModel
) {
	if (connectionState == HostConnectionState.Connected) {
		with(hostCombatViewModel) {
			assignDamageCombatant.value?.let {
				DamageCombatantDialog(::onDamageDialogSubmit, ::onDamageDialogCancel)
			}
			confirmDamage?.let { (damage, combatant) ->
				ConfirmDamageDialog(damage, combatant.name, ::onConfirmDamageDialogSubmit, ::onConfirmDamageDialogCancel)
			}
		}
	}
}

@Composable
private fun NextCombatantButton(hostCombatViewModel: HostCombatViewModel) {
	if (hostCombatViewModel.combatStarted) {
		FloatingActionButton(onClick = hostCombatViewModel::nextCombatant) {
			Icon(Icons.Default.FastForward, contentDescription = "Next Combatant")
		}
	}
}

@Composable
private fun TopBar(
	drawerState: DrawerState,
	hostCombatViewModel: HostCombatViewModel
) {
	var displayDropdown by remember(hostCombatViewModel.sessionId) { mutableStateOf(false) }

	val coroutineScope = rememberCoroutineScope()

	TopAppBar(
		title = {
			if (hostCombatViewModel.isSharing) {
				Text("Session ${hostCombatViewModel.sessionId}", color = MaterialTheme.colors.onPrimary)
			} else {
				Text("New combat", color = MaterialTheme.colors.onPrimary)
			}
		},
		navigationIcon = {
			BurgerMenuButtonForDrawer(drawerState)
		},
		actions = {
			if (!hostCombatViewModel.combatStarted) {
				IconButton(onClick = hostCombatViewModel::startCombat) {
					Icon(Icons.Default.PlayArrow, contentDescription = "Play")
				}
			}
			if (hostCombatViewModel.isSharing) {
				IconButton(onClick = {
					coroutineScope.launch {
						hostCombatViewModel.closeSession()
					}
				}) {
					Icon(Icons.Default.Close, contentDescription = "Close Session")
				}
			} else {
				IconButton(onClick = {
					coroutineScope.launch {
						hostCombatViewModel.shareCombat()
					}
				}) {
					Icon(Icons.Default.Share, contentDescription = "Start Sharing")
				}
			}
			IconButton(onClick = { displayDropdown = !displayDropdown }) {
				Icon(Icons.Default.MoreVert, "")
			}
			DropdownMenu(
				expanded = displayDropdown,
				onDismissRequest = { displayDropdown = false }
			) {
				DropdownMenuItem(onClick = hostCombatViewModel::showSessionId) {
					Text(text = "Show Session Id")
				}
			}
		},
		backgroundColor = MaterialTheme.colors.primarySurface,
	)
}