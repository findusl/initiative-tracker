package de.lehrbaum.initiativetracker.view.combat.host

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.lehrbaum.initiativetracker.R
import de.lehrbaum.initiativetracker.view.Constants.defaultPadding
import de.lehrbaum.initiativetracker.view.SwipeResponse
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Stable
interface HostCombatViewModel {
	val combatants: StateFlow<List<HostCombatantViewModel>>
	val hostEditCombatantViewModel: State<HostEditCombatantViewModel?>
	val combatStarted: StateFlow<Boolean>

	fun onCombatantSelected(hostCombatantViewModel: HostCombatantViewModel)
	fun onCombatantSwipedToEnd(hostCombatantViewModel: HostCombatantViewModel): SwipeResponse
	fun onCombatantSwipedToStart(hostCombatantViewModel: HostCombatantViewModel): SwipeResponse
	fun onAddNewPressed()
	fun nextCombatant()
}

@Composable
fun CombatHostScreen(hostCombatViewModel: HostCombatViewModel) {
	CombatantList(hostCombatViewModel)

	val hostEditCombatantViewModel = hostCombatViewModel.hostEditCombatantViewModel.value
	if (hostEditCombatantViewModel != null) {
		HostEditCombatantDialog(hostEditCombatantViewModel)
	}

	NextCombatantButton(hostCombatViewModel)
}

@Composable
@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
private fun CombatantList(hostCombatViewModel: HostCombatViewModel) {
	val combatants by hostCombatViewModel.combatants.collectAsState()
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
			.padding(defaultPadding)
			.fillMaxWidth()
		items(combatants, key = HostCombatantViewModel::id) { combatant ->
			val dismissState = handleDismissState(hostCombatViewModel, combatant)
			SwipeToDismiss(
				state = dismissState,
				dismissThresholds = { FractionalThreshold(0.3f) },
				background = { SwipeToDismissBackground(dismissState) }
			) {
				CharacterListElement(combatant, itemModifier.combinedClickable(
					onClick = { hostCombatViewModel.onCombatantSelected(combatant) }
				))
			}
		}

		addCreateNewCard(itemModifier, hostCombatViewModel)
	}
}

@Composable
private fun CharacterListElement(combatant: HostCombatantViewModel, modifier: Modifier = Modifier) {
	val backgroundColor by animateColorAsState(
		if (combatant.active) Color.Cyan else Color.White
	)
	Box(modifier = Modifier.background(backgroundColor)) {
		Card(elevation = 8.dp, modifier = modifier) {
			Row {
				Text(
					text = combatant.name, modifier = Modifier
						.padding(defaultPadding)
						.weight(1.0f, fill = true)
				)
				Text(text = combatant.initiativeString, modifier = Modifier.padding(defaultPadding))
			}
		}
	}
}

private fun LazyListScope.addCreateNewCard(
	modifier: Modifier,
	hostCombatViewModel: HostCombatViewModel
) {
	item {
		Card(elevation = 8.dp, modifier = modifier.clickable {
			hostCombatViewModel.onAddNewPressed()
		}) {
			Text(
				text = "Add Combatant",
				modifier = Modifier
					.padding(defaultPadding)
					.fillMaxWidth(),
				textAlign = TextAlign.Center
			)
		}
	}
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
private fun handleDismissState(
	hostCombatViewModel: HostCombatViewModel,
	combatant: HostCombatantViewModel
): DismissState {
	val dismissState = rememberDismissState(
		confirmStateChange = {
			when (it) {
				DismissValue.DismissedToEnd -> {
					hostCombatViewModel.onCombatantSwipedToEnd(combatant).dismissResponse
				}
				DismissValue.DismissedToStart -> {
					hostCombatViewModel.onCombatantSwipedToStart(combatant).dismissResponse
				}
				else -> {
					Napier.w { "Combatant $combatant was dismissed to state $it" }
					false
				}
			}
		}
	)
	return dismissState
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
private fun NextCombatantButton(hostCombatViewModel: HostCombatViewModel) {
	Box(modifier = Modifier.fillMaxSize()) {
		// Add the Floating Action Button
		val combatStarted by hostCombatViewModel.combatStarted.collectAsState()
		if (combatStarted) {
			FloatingActionButton(
				onClick = { hostCombatViewModel.nextCombatant() },
				modifier = Modifier
					.padding(all = 16.dp)
					.align(alignment = Alignment.BottomEnd),
			) {
				Icon(painterResource(R.drawable.baseline_fast_forward_24), contentDescription = "Next Combatant")
			}
		}
	}
}

@Preview(device = Devices.NEXUS_5, showBackground = true, showSystemUi = true)
@Composable
fun PreviewCombatHostScreen() {
	CombatHostScreen(MockHostCombatViewModel())
}

@Preview(device = Devices.NEXUS_5, showBackground = true, showSystemUi = true)
@Composable
fun PreviewHostEditCombatantScreen() {
	val viewModel = MockHostCombatViewModel()
	viewModel.hostEditCombatantViewModel.value = MockEditHostCombatantViewModel()
	CombatHostScreen(viewModel)
}

private val sampleCombatants = listOf(
	HostCombatantViewModel(1, "Combatant 1", 8, active = true),
	HostCombatantViewModel(2, "Combatant 2", 10)
).sortedByDescending { it.initiative }

private class MockHostCombatViewModel : HostCombatViewModel {
	override val combatants = MutableStateFlow(sampleCombatants)
	override val hostEditCombatantViewModel = mutableStateOf<HostEditCombatantViewModel?>(null)
	override val combatStarted = MutableStateFlow(true)

	override fun onCombatantSelected(hostCombatantViewModel: HostCombatantViewModel) {}

	override fun onCombatantSwipedToEnd(hostCombatantViewModel: HostCombatantViewModel) = SwipeResponse.SLIDE_BACK

	override fun onCombatantSwipedToStart(hostCombatantViewModel: HostCombatantViewModel) = SwipeResponse.SLIDE_OUT

	override fun onAddNewPressed() {}
	override fun nextCombatant() {}
}
