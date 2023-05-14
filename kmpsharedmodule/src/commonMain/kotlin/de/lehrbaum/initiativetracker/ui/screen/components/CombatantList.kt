package de.lehrbaum.initiativetracker.ui.screen.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.lehrbaum.initiativetracker.ui.model.shared.CombatantViewModel
import de.lehrbaum.initiativetracker.ui.screen.Constants

@Composable
@ExperimentalMaterialApi
@ExperimentalFoundationApi
fun CombatantList(
	combatants: List<CombatantViewModel>,
	onCombatantClicked: (CombatantViewModel) -> Unit,
	onCombatantLongClicked: (CombatantViewModel) -> Unit,
	onCreateNewClicked: (() -> Unit)? = null,
	dismissToEndAction: SwipeToDismissAction<CombatantViewModel>? = null,
	dismissToStartAction: SwipeToDismissAction<CombatantViewModel>? = null,
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
            SwipeToDismiss(dismissToEndAction, dismissToStartAction, combatant) {
                CombatantListElement(combatant, itemModifier.combinedClickable(
                    onClick = { onCombatantClicked(combatant) },
                    onLongClick = { onCombatantLongClicked(combatant) }
                ))
            }
        }

        if (onCreateNewClicked != null) {
            addCreateNewCard(itemModifier, "Add new combatant", onCreateNewClicked)
        }
    }
}

fun LazyListScope.addCreateNewCard(
    modifier: Modifier,
	label: String,
    onClicked: () -> Unit,
) {
    item {
        Card(elevation = 8.dp, modifier = modifier.clickable { onClicked() }) {
            Text(
                text = label,
                modifier = Modifier
                    .padding(Constants.defaultPadding)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}