package de.lehrbaum.initiativetracker.ui.host

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.DrawerState
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.window.Dialog
import de.lehrbaum.initiativetracker.bl.HostConnectionState
import de.lehrbaum.initiativetracker.ui.composables.BurgerMenuButtonForDrawer
import de.lehrbaum.initiativetracker.ui.composables.CombatantList
import de.lehrbaum.initiativetracker.ui.composables.ErrorComposable
import de.lehrbaum.initiativetracker.ui.composables.KeepScreenOn
import de.lehrbaum.initiativetracker.ui.composables.MyDropdownMenu
import de.lehrbaum.initiativetracker.ui.composables.ResettableState
import de.lehrbaum.initiativetracker.ui.composables.bindSnackbarState
import de.lehrbaum.initiativetracker.ui.composables.collectAsStateResettable
import de.lehrbaum.initiativetracker.ui.composables.rememberCoroutineScope
import de.lehrbaum.initiativetracker.ui.composables.rememberScaffoldState
import de.lehrbaum.initiativetracker.ui.composables.swipeToDelete
import de.lehrbaum.initiativetracker.ui.composables.swipeToDisable
import de.lehrbaum.initiativetracker.ui.composables.swipeToEnable
import de.lehrbaum.initiativetracker.ui.composables.swipeToJumpToTurn
import de.lehrbaum.initiativetracker.ui.damage.DamageCombatantDialog
import de.lehrbaum.initiativetracker.ui.edit.EditCombatantScreen
import de.lehrbaum.initiativetracker.ui.icons.FastForward
import de.lehrbaum.initiativetracker.ui.icons.Mic
import de.lehrbaum.initiativetracker.ui.keyevents.LocalShortcutManager
import de.lehrbaum.initiativetracker.ui.keyevents.defaultFocussed
import de.lehrbaum.initiativetracker.ui.keyevents.disposableShortcut
import de.lehrbaum.initiativetracker.ui.shared.ListDetailLayout
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
@ExperimentalMaterialApi
@ExperimentalFoundationApi
fun HostScreen(drawerState: DrawerState, hostCombatViewModel: HostCombatViewModel) {
	val hostCombatModelState = remember { mutableStateOf(hostCombatViewModel) }
	hostCombatModelState.value = hostCombatViewModel

	val connectionStateState = hostCombatViewModel.hostConnectionState.collectAsStateResettable(HostConnectionState.Connecting)

	if (hostCombatViewModel.isSharing) KeepScreenOn()

	val coroutineScope = rememberCoroutineScope(hostCombatViewModel)

	LocalShortcutManager.current?.disposableShortcut(' ', hostCombatViewModel::recordCommand)

	ListDetailLayout(
		list = {
			val scaffoldState = rememberScaffoldState(hostCombatViewModel, drawerState)
			scaffoldState.snackbarHostState.bindSnackbarState(hostCombatViewModel.snackbarState)

			Scaffold(
				scaffoldState = scaffoldState,
				topBar = { TopBar(drawerState, coroutineScope, hostCombatModelState.value) },
				floatingActionButton = { NextCombatantButton(hostCombatModelState.value) },
			) {
				MainContent(hostCombatModelState.value, connectionStateState)
			}

			val finishRecording: () -> Unit = { coroutineScope.launch { hostCombatViewModel.finishRecording() } }
			Dialogs(connectionStateState.value, hostCombatModelState.value, finishRecording)
		},
		detail = if (connectionStateState.value == HostConnectionState.Connected) {
			hostCombatViewModel.editCombatantViewModel.value?.let { { EditCombatantScreen(it) } }
		} else null,
		onDetailDismissRequest = { hostCombatViewModel.editCombatantViewModel.value?.cancel() },
	)

	hostCombatViewModel.ErrorComposable()
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
					combatants.collectAsState(persistentListOf()).value,
					isHost = true,
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
	hostCombatViewModel: HostCombatViewModel,
	finishRecording: () -> Unit,
) {
	if (connectionState == HostConnectionState.Connected) {
		with(hostCombatViewModel) {
			backendInputViewModel?.let {
				BackendInputDialog(it)
			}
			damageCombatantViewModel?.let {
				DamageCombatantDialog(it)
			}
			confirmDamage?.let { options ->
				ConfirmDamageDialog(options, ::onConfirmDamageDialogSubmit, ::onConfirmDamageDialogCancel)
			}
			if (isRecording) {
				FinishRecordingDialog(finishRecording)
			}
			if (isProcessingRecording) {
				Dialog(onDismissRequest = { cancelRecording() }) {
					Surface {
						Text("Processing Recording")
					}
				}
			}
		}
	}
}

@Composable
private fun HostCombatViewModel.FinishRecordingDialog(
	finishRecording: () -> Unit
) {
	Dialog(onDismissRequest = { cancelRecording() }) {
		Button(
			onClick = finishRecording,
			modifier = Modifier
				.defaultFocussed(this)
				.onKeyEvent {
					if (it.key == Key.Spacebar) {
						finishRecording()
						true
					} else false
				},
		) {
			Text("Finish Recording")
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

@Composable // TASK not skippable due to coroutineScope. should optimize by passing non suspend functions to sub-composable
private fun TopBar(
	drawerState: DrawerState,
	coroutineScope: CoroutineScope,
	hostCombatViewModel: HostCombatViewModel
) {
	var displayDropdown by remember(hostCombatViewModel) { mutableStateOf(false) }

	TopAppBar(
		title = {
			Text(hostCombatViewModel.title, color = MaterialTheme.colors.onPrimary)
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
			if (hostCombatViewModel.isRecordActionVisible) {
				IconButton(onClick = hostCombatViewModel::recordCommand) {
					Icon(Icons.Default.Mic, contentDescription = "Record")
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
			if (hostCombatViewModel.isSharing) {
				IconButton(onClick = { displayDropdown = !displayDropdown }) {
					Icon(Icons.Default.MoreVert, "")
				}
				MyDropdownMenu(
					expanded = displayDropdown,
					onDismissRequest = { displayDropdown = false }
				) {
					DropdownMenuItem(onClick = {
						displayDropdown = false
						hostCombatViewModel.showSessionId()
					}) {
						Text(text = "Show Session Id")
					}
				}
			}
		},
		backgroundColor = MaterialTheme.colors.primarySurface,
	)
}
