package de.lehrbaum.initiativetracker

import androidx.lifecycle.MutableNonNullLiveData
import androidx.lifecycle.NonNullLiveData
import androidx.lifecycle.ViewModel

class InitiativeViewModel : ViewModel() {

	private var nextId = 0L

	private val _combatants = MutableNonNullLiveData(emptyList<CombatantViewModel>())

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
		val newCombatant = CombatantViewModel(nextId++, "New Combatant", 99)
		_combatants.value = (_combatants.value + newCombatant).sortedDescending()
	}

	fun updateCombatant(updatedCombatant: CombatantViewModel) {
		_combatants.value = _combatants.value.map {
			if (it.id == updatedCombatant.id) updatedCombatant else it
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
