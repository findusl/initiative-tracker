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
import de.lehrbaum.initiativetracker.ui.edit.EditCombatantDialog
import de.lehrbaum.initiativetracker.ui.icons.FastForward
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
		topBar = topBarLambda(drawerState, hostCombatModel),
		floatingActionButton = rememberComposableLambda(hostCombatModel) {
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
			EditCombatantDialog(it)
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

private fun topBarLambda(drawerState: DrawerState, hostCombatModel: HostCombatModel): @Composable () -> Unit = {
	TopBar(
		drawerState,
		hostCombatModel.sessionId,
		hostCombatModel.isSharing,
		hostCombatModel.combatStarted,
		hostCombatModel::startCombat,
		hostCombatModel::closeSession,
		hostCombatModel::onShareClicked,
		hostCombatModel::showSessionId
	)
}

@Composable
private fun TopBar(
	drawerState: DrawerState,
	sessionId: Int,
	isSharing: Boolean,
	combatStarted: Boolean,
	startCombat: () -> Unit,
	closeSession: suspend () -> Unit,
	shareSession: suspend () -> Unit,
	showSessionId: () -> Unit,
) {
	var displayDropdown by remember(sessionId) { mutableStateOf(false) }

	val coroutineScope = rememberCoroutineScope()

	TopAppBar(
		title = {
			if (isSharing) {
				Text("Session ${sessionId}", color = MaterialTheme.colors.onPrimary)
			} else {
				Text("New combat", color = MaterialTheme.colors.onPrimary)
			}
		},
		navigationIcon = {
			BurgerMenuButtonForDrawer(drawerState)
		},
		actions = {
			if (!combatStarted) {
				IconButton(onClick = startCombat) {
					Icon(Icons.Default.PlayArrow, contentDescription = "Play")
				}
			}
			if (isSharing) {
				IconButton(onClick = {
					coroutineScope.launch {
						closeSession()
					}
				}) {
					Icon(Icons.Default.Close, contentDescription = "Close Session")
				}
			} else {
				IconButton(onClick = {
					coroutineScope.launch {
						shareSession()
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
				DropdownMenuItem(onClick = showSessionId) {
					Text(text = "Show Session Id")
				}
			}
		},
		backgroundColor = MaterialTheme.colors.primarySurface,
	)
}
