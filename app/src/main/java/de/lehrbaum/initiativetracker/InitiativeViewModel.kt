package de.lehrbaum.initiativetracker

import androidx.lifecycle.MutableNonNullLiveData
import androidx.lifecycle.NonNullLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import de.lehrbaum.initiativetracker.bestiary.BestiaryNetworkClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.plus

private const val DEFAULT_COMBATANT_TITLE = "New Combatant"

class InitiativeViewModel : DelegatingViewModel<InitiativeViewModel.Delegate>() {

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

	/**
	 * Set by the adapter. Should probably hide behind functions but easier for now.
	 */
	var currentlyEditingCombatant: CombatantViewModel? = null

	private var selectedCombatantId: Long? = null

	init {
		addCombatant()
		latestName = "New Combatant 2"
		addCombatant()
	}

	fun selectCombatant(combatantToSelect: CombatantViewModel?) {
		if (combatantToSelect?.editMode == true) return // nothing to do
		checkIfShouldSave()
		// could block so that the user still sees his changes in the background. Use coroutines. Future improvements
		selectedCombatantId = combatantToSelect?.id
		_combatants.value = _combatants.value.copyWithCorrectStates()
	}

	private fun checkIfShouldSave() {
		val currentlyEditingCombatant = currentlyEditingCombatant ?: return
		val oldSelectedCombatantViewModel = _combatants.value.first { it.id == currentlyEditingCombatant.id }
		val sanitizedEditingCombatant = currentlyEditingCombatant
			.copy(active = oldSelectedCombatantViewModel.active, editMode = oldSelectedCombatantViewModel.editMode)
		if (sanitizedEditingCombatant != oldSelectedCombatantViewModel) {
			delegate?.showSaveChangesDialog {
				updateCombatant(sanitizedEditingCombatant.copy(editMode = false))
			}
		}
	}

	fun addCombatant() {
		val newCombatant = CombatantViewModel(nextId++, latestName ?: DEFAULT_COMBATANT_TITLE, -10)
		_combatants.value = (_combatants.value + newCombatant)
			.sortedDescending()
			.copyWithCorrectStates()
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
			.copyWithCorrectStates()
	}

	fun nextTurn() {
		activeCombatantIndex = (activeCombatantIndex + 1) % combatants.value.size
		_combatants.value = _combatants.value.copyWithCorrectStates()
	}

	fun prevTurn() {
		activeCombatantIndex--
		if (activeCombatantIndex < 0) activeCombatantIndex = combatants.value.size - 1
		_combatants.value = _combatants.value.copyWithCorrectStates()
	}

	private fun List<CombatantViewModel>.copyWithCorrectStates(): List<CombatantViewModel> {
		return this.mapIndexed { index, combatantViewModel ->
			val shouldBeActive = activeCombatantIndex == index
			val shouldBeSelected = combatantViewModel.id == selectedCombatantId
			if (shouldBeActive != combatantViewModel.active || shouldBeSelected != combatantViewModel.editMode)
				combatantViewModel.copy(active = shouldBeActive, editMode = shouldBeSelected)
			else
				combatantViewModel
		}
	}

	interface Delegate {
		fun showSaveChangesDialog(onOkListener: () -> Unit)
	}
}
