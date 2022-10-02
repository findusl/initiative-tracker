package de.lehrbaum.initiativetracker

import androidx.lifecycle.*
import de.lehrbaum.initiativetracker.bestiary.BestiaryNetworkClient
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus

private const val DEFAULT_COMBATANT_TITLE = "New Combatant"
private const val TAG = "InitiativeViewModel"

class InitiativeViewModel : ViewModel() {

	private var nextId = 0L

	private var activeCombatantIndex = 0

	private val _combatants = MutableNonNullLiveData(emptyList<CombatantViewModel>())

	/**
	 * The most recent name set on a combatant. Default for new combatants, as often monsters have the same name.
	 */
	private var latestName: String? = null

	private val bestiaryNetworkClient = BestiaryNetworkClient()

	private val allMonstersFlow = bestiaryNetworkClient.monsters
		.flowOn(Dispatchers.IO)
		.stateIn(viewModelScope + Dispatchers.Main, SharingStarted.Eagerly, listOf())

	val allMonsterNamesLiveData = allMonstersFlow
		.map { monsters -> monsters.map { it.name }.toTypedArray() }
		.asLiveData(Dispatchers.IO)

	val combatants: NonNullLiveData<List<CombatantViewModel>>
		get() = _combatants

	init {
		addCombatant()
		addCombatant()
		viewModelScope.launch {
			allMonstersFlow.collect {
				Napier.i("Loaded ${it.size} monsters ", tag = TAG)
			}
		}
	}

	fun selectCombatant(selectedCombatant: CombatantViewModel?) {
		_combatants.value = _combatants.value.map {
			val shouldBeSelected = it.id == selectedCombatant?.id
			if (it.editMode != shouldBeSelected) it.copy(editMode = shouldBeSelected) else it
		}
	}

	fun addCombatant() {
		val newCombatant = CombatantViewModel(nextId++, latestName ?: DEFAULT_COMBATANT_TITLE, 99)
		_combatants.value = (_combatants.value + newCombatant)
			.sortedDescending()
			.copyWithCorrectActiveState()
	}

	fun updateCombatant(updatedCombatant: CombatantViewModel) {
		_combatants.value = _combatants.value.map {
      if (it.id == updatedCombatant.id) {
				if (it.name != updatedCombatant.name) {
					latestName = updatedCombatant.name
				}
				updatedCombatant
			} else {
				it
			}
		}
			.sortedDescending()
			.copyWithCorrectActiveState()
	}

	fun nextTurn() {
		activeCombatantIndex = (activeCombatantIndex + 1) % combatants.value.size
		_combatants.value = _combatants.value.copyWithCorrectActiveState()
	}

	fun prevTurn() {
		activeCombatantIndex--
		if (activeCombatantIndex < 0) activeCombatantIndex = combatants.value.size - 1
		_combatants.value = _combatants.value.copyWithCorrectActiveState()
	}

	private fun List<CombatantViewModel>.copyWithCorrectActiveState(): List<CombatantViewModel> {
		return this.mapIndexed { index, combatantViewModel ->
			val shouldBeActive = activeCombatantIndex == index
			if (shouldBeActive != combatantViewModel.active)
				combatantViewModel.copy(active = shouldBeActive)
			else
				combatantViewModel
		}
	}
}

