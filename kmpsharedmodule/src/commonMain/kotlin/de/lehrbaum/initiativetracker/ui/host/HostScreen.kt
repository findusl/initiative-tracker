package de.lehrbaum.initiativetracker.ui.host

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.*
import de.lehrbaum.initiativetracker.bl.HostConnectionState
import de.lehrbaum.initiativetracker.ui.composables.*
import de.lehrbaum.initiativetracker.ui.edit.EditCombatantScreen
import de.lehrbaum.initiativetracker.ui.icons.FastForward
import de.lehrbaum.initiativetracker.ui.shared.ListDetailLayout
import kotlinx.coroutines.launch

@Composable
@ExperimentalMaterialApi
@ExperimentalFoundationApi
fun HostScreen(drawerState: DrawerState, hostCombatModel: HostCombatModel) {
	val hostCombatModelState = remember { mutableStateOf(hostCombatModel) }
	hostCombatModelState.value = hostCombatModel

	val connectionStateState = hostCombatModel.hostConnectionState.collectAsState(HostConnectionState.Connecting)

	ListDetailLayout(
		list = {
			val scaffoldState = rememberScaffoldState()
			scaffoldState.snackbarHostState.bindSnackbarState(hostCombatModel.snackbarState)

			Scaffold(
				scaffoldState = scaffoldState,
				topBar = { TopBar(drawerState, hostCombatModelState.value) },
				floatingActionButton = { NextCombatantButton(hostCombatModelState.value) },
			) {
				MainContent(hostCombatModelState.value, connectionStateState)
			}

			if (connectionStateState.value == HostConnectionState.Connected) {
				hostCombatModel.assignDamageCombatant.value?.let {
					DamageCombatantDialog(hostCombatModel::onDamageDialogSubmit, hostCombatModel::onDamageDialogCancel)
				}
			}
		},
		detail = if (connectionStateState.value == HostConnectionState.Connected) {
			hostCombatModel.editCombatantModel.value?.let { { EditCombatantScreen(it) } }
		} else null
	)
}

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
private fun MainContent(
	hostCombatModel: HostCombatModel,
	connectionStateState: State<HostConnectionState>
) {
	hostCombatModel.apply {
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
			is HostConnectionState.Disconnected -> Text("Disconnected! Reason: ${connectionState.reason}")
		}
	}
}

@Composable
private fun NextCombatantButton(hostCombatModel: HostCombatModel) {
	if (hostCombatModel.combatStarted) {
		FloatingActionButton(onClick = hostCombatModel::nextCombatant) {
			Icon(Icons.Default.FastForward, contentDescription = "Next Combatant")
		}
	}
}

@Composable
private fun TopBar(
	drawerState: DrawerState,
	hostCombatModel: HostCombatModel
) {
	var displayDropdown by remember(hostCombatModel.sessionId) { mutableStateOf(false) }

	val coroutineScope = rememberCoroutineScope()

	TopAppBar(
		title = {
			if (hostCombatModel.isSharing) {
				Text("Session ${hostCombatModel.sessionId}", color = MaterialTheme.colors.onPrimary)
			} else {
				Text("New combat", color = MaterialTheme.colors.onPrimary)
			}
		},
		navigationIcon = {
			BurgerMenuButtonForDrawer(drawerState)
		},
		actions = {
			if (!hostCombatModel.combatStarted) {
				IconButton(onClick = hostCombatModel::startCombat) {
					Icon(Icons.Default.PlayArrow, contentDescription = "Play")
				}
			}
			if (hostCombatModel.isSharing) {
				IconButton(onClick = {
					coroutineScope.launch {
						hostCombatModel.closeSession()
					}
				}) {
					Icon(Icons.Default.Close, contentDescription = "Close Session")
				}
			} else {
				IconButton(onClick = {
					coroutineScope.launch {
						hostCombatModel.shareCombat()
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
				DropdownMenuItem(onClick = hostCombatModel::showSessionId) {
					Text(text = "Show Session Id")
				}
			}
		},
		backgroundColor = MaterialTheme.colors.primarySurface,
	)
}
