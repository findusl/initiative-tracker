package de.lehrbaum.initiativetracker

import androidx.lifecycle.MutableNonNullLiveData
import androidx.lifecycle.NonNullLiveData
import androidx.lifecycle.ViewModel

private const val DEFAULT_COMBATANT_TITLE = "New Combatant"

class InitiativeViewModel : ViewModel() {

	private var nextId = 0L

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
			if (it.selected != shouldBeSelected) it.copy(selected = shouldBeSelected) else it
		}.sortedDescending()
	}

	fun addCombatant() {
		val newCombatant = CombatantViewModel(nextId++, latestName ?: DEFAULT_COMBATANT_TITLE, 99)
		_combatants.value = (_combatants.value + newCombatant).sortedDescending()
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
		}.sortedDescending()
	}
}

data class CombatantViewModel(
	val id: Long,
	val name: String,
	val initiative: Short,
	val selected: Boolean = false,
) : Comparable<CombatantViewModel> {

	val initiativeString: String
		get() = initiative.toString()

	override fun compareTo(other: CombatantViewModel): Int {
		return initiative - other.initiative
	}
}
