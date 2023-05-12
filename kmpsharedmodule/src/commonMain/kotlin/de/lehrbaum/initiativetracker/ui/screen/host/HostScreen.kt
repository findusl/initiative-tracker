@file:Suppress("OPT_IN_IS_NOT_ENABLED")

package de.lehrbaum.initiativetracker.ui.screen.host

import FastForward
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.lehrbaum.initiativetracker.bl.HostConnectionState
import de.lehrbaum.initiativetracker.ui.model.CombatantViewModel
import de.lehrbaum.initiativetracker.ui.model.SwipeResponse
import de.lehrbaum.initiativetracker.ui.model.host.HostCombatModel
import de.lehrbaum.initiativetracker.ui.screen.Constants
import de.lehrbaum.initiativetracker.ui.screen.components.*
import de.lehrbaum.initiativetracker.ui.screen.edit.HostEditCombatantDialog

@Composable
fun HostScreen(drawerState: DrawerState, hostCombatModel: HostCombatModel) {
    val scaffoldState = rememberScaffoldState()
	val connectionState = hostCombatModel.hostConnectionState.collectAsState(HostConnectionState.Connecting).value
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { TopBar(drawerState, hostCombatModel) },
        snackbarHost = { it.showSnackbar(hostCombatModel.snackbarState) },
        floatingActionButton = {
            NextCombatantButton(
                hostCombatModel.combatStarted,
                hostCombatModel::nextCombatant
            )
        }
    ) {
		when(connectionState) {
			HostConnectionState.Connected -> {
				CombatantList(
					hostCombatModel.combatants.collectAsState(emptyList()).value,
					hostCombatModel::onCombatantPressed,
					hostCombatModel::onCombatantLongPressed,
					hostCombatModel::onAddNewPressed,
					hostCombatModel::deleteCombatant,
				)
			}
			HostConnectionState.Connecting -> Text("Connecting")
			is HostConnectionState.Disconnected -> Text("Disconnected! Reason: ${connectionState.reason}")
		}
    }

	if (connectionState == HostConnectionState.Connected) {
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
private fun NextCombatantButton(combatStarted: Boolean, onClicked : () -> Unit) {
    if (combatStarted) {
        FloatingActionButton(onClick = { onClicked() }, ) {
            Icon(Icons.Default.FastForward, contentDescription = "Next Combatant")
        }
    }
}

@Composable
@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
private fun CombatantList(
    combatants: List<CombatantViewModel>,
    onCombatantClicked: (CombatantViewModel) -> Unit,
    onCombatantLongClicked: (CombatantViewModel) -> Unit,
    onCreateNewClicked: (() -> Unit)? = null,
    deleteCombatant: (CombatantViewModel) -> Unit,
) {
    val listState = rememberLazyListState()

    // Find the index of the active character
    val activeCharacterIndex = combatants.indexOfFirst { it.active }

    // Animate scrolling to the active character's position
    LaunchedEffect(activeCharacterIndex) {
        if (activeCharacterIndex != -1) {
            listState.animateScrollToItem(activeCharacterIndex, scrollOffset = -200)
        }
    }
    LazyColumn(state = listState) {
        val itemModifier = Modifier
            .padding(Constants.defaultPadding)
            .fillMaxWidth()
        items(combatants, key = CombatantViewModel::id) { combatant ->

            SwipeToDismiss(
				dismissToEndAction = null,
				dismissToStartAction = SwipeToDismissAction(
					Color.Red,
					Icons.Default.Delete,
					contentDescription = "Delete Combatant",
					action = {
						deleteCombatant(combatant)
						SwipeResponse.SLIDE_OUT
					}
				)
            ) {
                CombatantListElement(combatant, itemModifier.combinedClickable(
                    onClick = { onCombatantClicked(combatant) },
                    onLongClick = { onCombatantLongClicked(combatant) }
                ))
            }
        }

        if (onCreateNewClicked != null) {
            addCreateNewCard(itemModifier, onCreateNewClicked)
        }
    }
}

private fun LazyListScope.addCreateNewCard(
    modifier: Modifier,
    onClicked: () -> Unit,
) {
    item {
        Card(elevation = 8.dp, modifier = modifier.clickable { onClicked() }) {
            Text(
                text = "Add Combatant",
                modifier = Modifier
                    .padding(Constants.defaultPadding)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun TopBar(
    drawerState: DrawerState,
    hostCombatModel: HostCombatModel
) {
    var displayDropdown by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
			if (hostCombatModel.isSharing) {
				Text("Session ${hostCombatModel.sessionId}", color = MaterialTheme.colors.onPrimary)
			} else {
				Text("Host new combat", color = MaterialTheme.colors.onPrimary)
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
                IconButton(onClick = hostCombatModel::closeSession) {
                    Icon(Icons.Default.Close, contentDescription = "Close Session")
                }
            } else {
                IconButton(onClick = hostCombatModel::onShareClicked) {
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
