package de.lehrbaum.initiativetracker.ui.composables

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
import de.lehrbaum.initiativetracker.dtos.CombatantModel
import de.lehrbaum.initiativetracker.ui.Constants
import de.lehrbaum.initiativetracker.ui.shared.CombatantViewModel

@Composable
@ExperimentalMaterialApi
@ExperimentalFoundationApi
fun CombatantList(
	combatants: List<CombatantViewModel>,
	isHost: Boolean,
	onCombatantClicked: (CombatantViewModel) -> Unit,
	onCombatantLongClicked: (CombatantViewModel) -> Unit,
	onCreateNewClicked: (() -> Unit)? = null,
	dismissToEndAction: @Composable (CombatantViewModel) -> SwipeToDismissAction<CombatantViewModel>? = { null },
	dismissToStartAction: @Composable (CombatantViewModel) -> SwipeToDismissAction<CombatantViewModel>? = { null },
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
        items(combatants, key = { it.id.id }, contentType = { CombatantModel::class }) { combatant ->
            SwipeToDismiss(dismissToEndAction(combatant), dismissToStartAction(combatant), combatant) {
                CombatantListElement(combatant, isHost, Modifier.combinedClickable(
                    onClick = { onCombatantClicked(combatant) },
                    onLongClick = { onCombatantLongClicked(combatant) }
                ))
            }
        }

        if (onCreateNewClicked != null) {
            addCreateNewCard("Add new combatant", onCreateNewClicked)
        } else if (combatants.isEmpty()) {
			// so that it is not completely empty, looks like an error
			item(key = combatants) {
				Text("No Combatants in the combat")
			}
		}
    }
}

fun LazyListScope.addCreateNewCard(
	label: String,
    onClicked: () -> Unit,
	modifier: Modifier = Modifier,
) {
    item {
        Card(
			elevation = 8.dp,
			modifier = modifier
				// the order of modifiers is relevant. If the padding is before clickable only the inner part is clickable
				.clickable { onClicked() }
				.padding(Constants.defaultPadding)
		) {
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