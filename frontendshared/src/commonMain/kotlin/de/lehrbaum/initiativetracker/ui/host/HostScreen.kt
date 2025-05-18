package de.lehrbaum.initiativetracker.ui.host

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import de.lehrbaum.initiativetracker.networking.hosting.HostConnectionState
import de.lehrbaum.initiativetracker.ui.Constants
import de.lehrbaum.initiativetracker.ui.GeneralDialog
import de.lehrbaum.initiativetracker.ui.composables.BurgerMenuButtonForDrawer
import de.lehrbaum.initiativetracker.ui.composables.CombatantList
import de.lehrbaum.initiativetracker.ui.composables.ErrorComposable
import de.lehrbaum.initiativetracker.ui.composables.Guide
import de.lehrbaum.initiativetracker.ui.composables.KeepScreenOn
import de.lehrbaum.initiativetracker.ui.composables.MyDropdownMenu
import de.lehrbaum.initiativetracker.ui.composables.OkCancelButtonRow
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

	val connectionStateState =
		hostCombatViewModel.hostConnectionState.collectAsStateResettable(HostConnectionState.Connecting)

	if (hostCombatViewModel.isSharing) KeepScreenOn()

	val coroutineScope = rememberCoroutineScope(hostCombatViewModel)

	LocalShortcutManager.current?.disposableShortcut(' ', hostCombatViewModel::recordCommand)

	ListDetailLayout(
		list = {
			val scaffoldState = rememberScaffoldState(hostCombatViewModel, drawerState)
			scaffoldState.snackbarHostState.bindSnackbarState(hostCombatViewModel.snackbarState)

			Scaffold(
				scaffoldState = scaffoldState,
				topBar = { TopBar(drawerState, coroutineScope, hostCombatViewModel) },
				floatingActionButton = { NextCombatantButton(hostCombatViewModel) },
			) {
				MainContent(hostCombatViewModel, connectionStateState)
			}

			val finishRecording: () -> Unit = { coroutineScope.launch { hostCombatViewModel.finishRecording() } }
			Dialogs(connectionStateState.value, hostCombatViewModel, finishRecording)
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
				val combatantsList = combatants.collectAsState(persistentListOf()).value

				Column {

					GuideBanners(combatantsList.isNotEmpty())

					CombatantList(
						combatantsList,
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
			}

			HostConnectionState.Connecting -> Text("Connecting")
			is HostConnectionState.Disconnected -> {
				Column {
					Text("Disconnected! Reason: ${connectionState.reason}")
					Button({ connectionStateState.reset() }) {
						Text("Restart Connection")
					}
					Text("(LocalHostServer may take up to 30 seconds to notice a missing host)")
				}
			}
		}
	}
}

@Composable
private fun HostCombatViewModel.GuideBanners(hasCombatants: Boolean) {
	if (hasCombatants && !combatStarted) {
		Guide(
			text = "Start the combat for initiative and damaging to work.",
			guideKey = "start_combat_guide"
		)
	}

	if (combatStarted) {
		Guide(
			text = "You can swipe combatants right and left",
			guideKey = "swipe_combatants_guide"
		)
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
				ConfirmDamageDialog(
					options,
					onDamageApplied = ::onConfirmDamageDialogSubmit,
					onDismiss = ::onConfirmDamageDialogCancel
				)
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
			if (showResetConfirmation) {
				ResetConfirmationDialog(
					onConfirm = { onResetResponse(true) }
				) { onResetResponse(false) }
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
					if (it.key == Key.Spacebar && it.type == KeyEventType.KeyDown) {
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
private fun ResetConfirmationDialog(
	onConfirm: () -> Unit,
	onCancel: () -> Unit
) {
	GeneralDialog(onDismissRequest = onCancel) {
		Column(modifier = Modifier.padding(16.dp)) {
			Text(
				text = "Reset Combat",
				style = MaterialTheme.typography.h6
			)
			Spacer(modifier = Modifier.height(8.dp))
			Text(
				text = "Are you sure you want to reset the combat? This will remove all monsters, stop initiative, and return to a not running combat state.",
				style = MaterialTheme.typography.body1
			)
			Spacer(modifier = Modifier.height(16.dp))
			OkCancelButtonRow(
				submittable = true,
				onCancel = onCancel,
				onSubmit = onConfirm
			)
		}
	}
}

@Composable
private fun NextCombatantButton(hostCombatViewModel: HostCombatViewModel) {
	if (hostCombatViewModel.combatStarted) {
		FloatingActionButton(onClick = hostCombatViewModel::nextCombatant) {
			Icon(Icons.Default.SkipNext, contentDescription = "Next Combatant")
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
			} else {
				IconButton(onClick = {
					coroutineScope.launch {
						hostCombatViewModel.resetCombat()
					}
				}) {
					Icon(Icons.Default.Delete, contentDescription = "Reset Combat")
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
			if (hostCombatViewModel.isSharing || hostCombatViewModel.showAutoConfirmDamageToggle) {
				IconButton(onClick = { displayDropdown = !displayDropdown }) {
					Icon(Icons.Default.MoreVert, "")
				}
				MyDropdownMenu(
					expanded = displayDropdown,
					onDismissRequest = { displayDropdown = false }
				) {
					if (hostCombatViewModel.isSharing) {
						DropdownMenuItem(onClick = {
							displayDropdown = false
							hostCombatViewModel.showSessionId()
						}) {
							Text(text = "Show Session Id")
						}
					}
					if (hostCombatViewModel.showAutoConfirmDamageToggle) {
						DropdownMenuItem(onClick = {
							hostCombatViewModel.autoConfirmDamagePressed()
						}) {
							Checkbox(hostCombatViewModel.autoConfirmDamage, onCheckedChange = null)
							Text(text = "Autoconfirm Damage", modifier = Modifier.padding(start = Constants.defaultPadding))
						}
					}
				}
			}
		},
		backgroundColor = MaterialTheme.colors.primarySurface,
	)
}
