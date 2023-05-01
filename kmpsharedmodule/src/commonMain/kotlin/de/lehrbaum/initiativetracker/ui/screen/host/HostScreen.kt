@file:Suppress("OPT_IN_IS_NOT_ENABLED")

package de.lehrbaum.initiativetracker.ui.screen.host

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Card
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissState
import androidx.compose.material.DismissValue
import androidx.compose.material.DrawerState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.primarySurface
import androidx.compose.material.rememberDismissState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.lehrbaum.initiativetracker.ui.model.CombatantViewModel
import de.lehrbaum.initiativetracker.ui.model.SwipeResponse
import de.lehrbaum.initiativetracker.ui.model.host.HostCombatModel
import de.lehrbaum.initiativetracker.ui.screen.Constants
import de.lehrbaum.initiativetracker.ui.screen.components.BurgerMenuButtonForDrawer
import de.lehrbaum.initiativetracker.ui.screen.components.CombatantListElement
import de.lehrbaum.initiativetracker.ui.screen.components.DamageCombatantDialog
import de.lehrbaum.initiativetracker.ui.screen.components.showSnackbar
import io.github.aakira.napier.Napier

@Composable
fun HostScreen(drawerState: DrawerState, hostCombatModel: HostCombatModel) {
    val scaffoldState = rememberScaffoldState()
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { TopBar(drawerState) },
        snackbarHost = { it.showSnackbar(hostCombatModel.snackbarState) },
        floatingActionButton = {
            NextCombatantButton(hostCombatModel.combatStarted.collectAsState().value, hostCombatModel::nextCombatant)
        }
    ) {
        CombatantList(
            hostCombatModel.combatants.collectAsState().value,
            hostCombatModel::onCombatantPressed,
            hostCombatModel::onCombatantLongPressed,
            hostCombatModel::onAddNewPressed,
            hostCombatModel::onCombatantSwipedToEnd,
            hostCombatModel::onCombatantSwipedToStart,
        )
    }

    hostCombatModel.assignDamageCombatant.value?.let {
        DamageCombatantDialog(hostCombatModel::onDamageDialogSubmit) {
            hostCombatModel.assignDamageCombatant.value = null
        }
    }

    /*hostCombatViewModel.hostEditCombatantViewModel.value?.let {
        HostEditCombatantDialog(it)
    }*/
}

@Composable
private fun NextCombatantButton(combatStarted: Boolean, onClicked : () -> Unit) {
    if (combatStarted) {
        FloatingActionButton(onClick = { onClicked() }, ) {
            Icon(Icons.Default.ArrowForward, contentDescription = "Next Combatant")
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
    dismissedToEnd: (CombatantViewModel) -> SwipeResponse,
    dismissedToStart: (CombatantViewModel) -> SwipeResponse,
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
            val dismissState = handleDismissState(
                dismissedToEnd = { dismissedToEnd(combatant) },
                dismissedToStart = { dismissedToStart(combatant) }
            )
            SwipeToDismiss(
                state = dismissState,
                dismissThresholds = { FractionalThreshold(0.3f) },
                background = { SwipeToDismissBackground(dismissState) }
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
@OptIn(ExperimentalMaterialApi::class)
private fun handleDismissState(
    dismissedToEnd: () -> SwipeResponse,
    dismissedToStart: () -> SwipeResponse,
): DismissState {
    return rememberDismissState(
        confirmStateChange = {
            when (it) {
                DismissValue.DismissedToEnd -> {
                    dismissedToEnd().dismissResponse
                }
                DismissValue.DismissedToStart -> {
                    dismissedToStart().dismissResponse
                }
                else -> {
                    Napier.w { "Dismissed to unknown state $it" }
                    false
                }
            }
        }
    )
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
private fun SwipeToDismissBackground(dismissState: DismissState) {
    val direction = dismissState.dismissDirection ?: return

    val color by animateColorAsState(
        when (dismissState.targetValue) {
            DismissValue.Default -> Color.LightGray
            DismissValue.DismissedToEnd -> Color.Green
            DismissValue.DismissedToStart -> Color.Red
        }
    )
    val alignment = when (direction) {
        DismissDirection.StartToEnd -> Alignment.CenterStart
        DismissDirection.EndToStart -> Alignment.CenterEnd
    }
    val icon = when (direction) {
        DismissDirection.StartToEnd -> Icons.Default.Done
        DismissDirection.EndToStart -> Icons.Default.Delete
    }
    val scale by animateFloatAsState(
        if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f
    )

    Box(
        Modifier
            .fillMaxSize()
            .background(color)
            .padding(horizontal = 20.dp),
        contentAlignment = alignment
    ) {
        Icon(
            icon,
            contentDescription = "Localized description",
            modifier = Modifier.scale(scale)
        )
    }
}

@Composable
private fun TopBar(drawerState: DrawerState) {
    TopAppBar(
        title = { Text("Host new combat", color = MaterialTheme.colors.onPrimary) },
        navigationIcon = {
            BurgerMenuButtonForDrawer(drawerState)
        },
        backgroundColor = MaterialTheme.colors.primarySurface
    )
}
