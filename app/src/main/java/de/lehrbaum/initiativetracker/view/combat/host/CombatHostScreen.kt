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
import de.lehrbaum.initiativetracker.ui.model.CombatantViewModel
import de.lehrbaum.initiativetracker.ui.screen.Constants.defaultPadding
import de.lehrbaum.initiativetracker.ui.model.SwipeResponse
import de.lehrbaum.initiativetracker.ui.screen.components.CombatantListElement
import de.lehrbaum.initiativetracker.ui.screen.components.DamageCombatantDialog
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Stable
interface HostCombatViewModel {
	val combatants: StateFlow<List<CombatantViewModel>>
	val hostEditCombatantViewModel: State<HostEditCombatantViewModel?>
	val assignDamageCombatant: MutableState<CombatantViewModel?>
	val combatStarted: StateFlow<Boolean>

	fun onCombatantPressed(combatantViewModel: CombatantViewModel)
	fun onCombatantLongPressed(combatant: CombatantViewModel)
	fun onCombatantSwipedToEnd(combatantViewModel: CombatantViewModel): SwipeResponse
	fun onCombatantSwipedToStart(combatantViewModel: CombatantViewModel): SwipeResponse
	fun onDamageDialogSubmit(damage: Int)
	fun onAddNewPressed()
	fun nextCombatant()
}

@Composable
fun CombatHostScreen(hostCombatViewModel: HostCombatViewModel) {
	CombatantList(hostCombatViewModel)

	hostCombatViewModel.hostEditCombatantViewModel.value?.let {
		HostEditCombatantDialog(it)
	}

	hostCombatViewModel.assignDamageCombatant.value?.let {
		DamageCombatantDialog(hostCombatViewModel::onDamageDialogSubmit) {
			hostCombatViewModel.assignDamageCombatant.value = null
		}
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
		items(combatants, key = CombatantViewModel::id) { combatant ->
			val dismissState = handleDismissState(hostCombatViewModel, combatant)
			SwipeToDismiss(
				state = dismissState,
				dismissThresholds = { FractionalThreshold(0.3f) },
				background = { SwipeToDismissBackground(dismissState) }
			) {
				CombatantListElement(combatant, itemModifier.combinedClickable(
					onClick = { hostCombatViewModel.onCombatantPressed(combatant) },
					onLongClick = { hostCombatViewModel.onCombatantLongPressed(combatant) }
				))
			}
		}

		addCreateNewCard(itemModifier, hostCombatViewModel)
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
	combatant: CombatantViewModel
): DismissState {
	return rememberDismissState(
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
	CombatantViewModel(1, "Combatant 1", 8, 20, 20, active = true),
	CombatantViewModel(2, "Combatant 2", 10, 20, 10)
).sortedByDescending { it.initiative }

private class MockHostCombatViewModel : HostCombatViewModel {
	override val combatants = MutableStateFlow(sampleCombatants)
	override val hostEditCombatantViewModel = mutableStateOf<HostEditCombatantViewModel?>(null)
	override val assignDamageCombatant = mutableStateOf<CombatantViewModel?>(null)
	override val combatStarted = MutableStateFlow(true)

	override fun onCombatantPressed(combatantViewModel: CombatantViewModel) {}

	override fun onCombatantSwipedToEnd(combatantViewModel: CombatantViewModel) = SwipeResponse.SLIDE_BACK

	override fun onCombatantSwipedToStart(combatantViewModel: CombatantViewModel) = SwipeResponse.SLIDE_OUT
	override fun onDamageDialogSubmit(damage: Int) {}

	override fun onAddNewPressed() {}
	override fun nextCombatant() {}
	override fun onCombatantLongPressed(combatant: CombatantViewModel) {}
}
