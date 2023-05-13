package de.lehrbaum.initiativetracker.ui.screen.host

import FastForward
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import de.lehrbaum.initiativetracker.bl.HostConnectionState
import de.lehrbaum.initiativetracker.ui.model.CombatantViewModel
import de.lehrbaum.initiativetracker.ui.model.SwipeResponse
import de.lehrbaum.initiativetracker.ui.model.host.HostCombatModel
import de.lehrbaum.initiativetracker.ui.screen.components.*
import de.lehrbaum.initiativetracker.ui.screen.edit.HostEditCombatantDialog
import kotlinx.coroutines.launch

@Composable
@ExperimentalMaterialApi
@ExperimentalFoundationApi
fun HostScreen(drawerState: DrawerState, hostCombatModel: HostCombatModel) {
	val connectionStateState = hostCombatModel.hostConnectionState.collectAsState(HostConnectionState.Connecting)
	val scaffoldState = rememberScaffoldState()
	scaffoldState.snackbarHostState.bindSnackbarState(hostCombatModel.snackbarState)

	Scaffold(
		scaffoldState = scaffoldState,
		topBar = { TopBar(drawerState, hostCombatModel) },
		floatingActionButton = {
			NextCombatantButton(
				hostCombatModel.combatStarted,
				hostCombatModel::nextCombatant
			)
		}
	) {
		val connectionState = connectionStateState.value
		when (connectionState) {
			HostConnectionState.Connected -> {
				CombatantList(
					hostCombatModel.combatants.collectAsState(emptyList()).value,
					hostCombatModel::onCombatantClicked,
					hostCombatModel::onCombatantLongClicked,
					hostCombatModel::onAddNewPressed,
					dismissToEndAction = null,
					dismissToStartAction = swipeToDeleteAction(hostCombatModel::deleteCombatant)
				)
			}

			HostConnectionState.Connecting -> Text("Connecting")
			is HostConnectionState.Disconnected -> Text("Disconnected! Reason: ${connectionState.reason}")
		}
	}

	if (connectionStateState.value == HostConnectionState.Connected) {
		hostCombatModel.assignDamageCombatant.value?.let {
			DamageCombatantDialog(hostCombatModel::onDamageDialogSubmit) {
				hostCombatModel.assignDamageCombatant.value = null
			}
		}

		hostCombatModel.editCombatantModel.value?.let {
			HostEditCombatantDialog(it)
		}
	}
}

@Composable
private fun NextCombatantButton(combatStarted: Boolean, onClicked: () -> Unit) {
	if (combatStarted) {
		FloatingActionButton(onClick = { onClicked() }) {
			Icon(Icons.Default.FastForward, contentDescription = "Next Combatant")
		}
	}
}

private fun swipeToDeleteAction(deleteCombatant: (CombatantViewModel) -> Unit): SwipeToDismissAction<CombatantViewModel> =
	SwipeToDismissAction(
		Color.Red,
		Icons.Default.Delete,
		contentDescription = "Delete Combatant",
		action = { combatant ->
			deleteCombatant(combatant)
			SwipeResponse.SLIDE_OUT
		}
	)

@Composable
private fun TopBar(
	drawerState: DrawerState,
	hostCombatModel: HostCombatModel
) {
	var displayDropdown by remember { mutableStateOf(false) }

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
						hostCombatModel.onShareClicked()
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
