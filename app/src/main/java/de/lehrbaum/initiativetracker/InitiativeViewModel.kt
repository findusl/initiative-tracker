package de.lehrbaum.initiativetracker

import androidx.lifecycle.MutableNonNullLiveData
import androidx.lifecycle.NonNullLiveData
import androidx.lifecycle.ViewModel

private const val DEFAULT_COMBATANT_TITLE = "New Combatant"

class InitiativeViewModel : ViewModel() {

	private var nextId = 0L

	private var activeCombatantIndex = 0

	private val _combatants = MutableNonNullLiveData(emptyList<CombatantViewModel>())

	/**
	 * The most recent name set on a combatant. It is used for new combatants, as often monsters have the same name.
	 */
	private var latestName: String? = null

	val combatants: NonNullLiveData<List<CombatantViewModel>>
		get() = _combatants

	init {
		addCombatant()
		addCombatant()
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

data class CombatantViewModel(
	val id: Long,
	val name: String,
	val initiative: Short,
	val editMode: Boolean = false,
	var active: Boolean = false
) : Comparable<CombatantViewModel> {

	val initiativeString: String
		get() = initiative.toString()

	override fun compareTo(other: CombatantViewModel): Int {
		var order = initiative - other.initiative
		if (order == 0)
			order = (id - other.id).toInt()
		return order
	}
}
