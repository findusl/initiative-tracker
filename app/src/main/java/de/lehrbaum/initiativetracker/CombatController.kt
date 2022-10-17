package de.lehrbaum.initiativetracker

import androidx.annotation.MainThread
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

private const val DEFAULT_COMBATANT_TITLE = "New Combatant"

@MainThread
class CombatController {
	private var nextId = 0L

	/**
	 * The most recent name set on a combatant. Default for new combatants, as often monsters have the same name.
	 */
	private var latestName: String? = null

	private var combatantCount = 0

	private val _currentCombat = MutableStateFlow(emptySequence<CombatantModel>())
	val currentCombat: StateFlow<Sequence<CombatantModel>>
		get() = _currentCombat

	private val _activeCombatantIndex = MutableStateFlow(0)
	val activeCombatantIndex: StateFlow<Int>
		get() = _activeCombatantIndex

	fun nextTurn() {
		_activeCombatantIndex.value = (activeCombatantIndex.value + 1) % combatantCount
	}

	fun prevTurn() {
		var newActiveCombatant = activeCombatantIndex.value - 1
		if (newActiveCombatant < 0) newActiveCombatant = combatantCount - 1
		_activeCombatantIndex.value = newActiveCombatant
	}

	fun addCombatant() {
		val newCombatant = CombatantModel(nextId++, latestName ?: DEFAULT_COMBATANT_TITLE, -99)
		_currentCombat.value = (_currentCombat.value + newCombatant).sortByInitiative()
		combatantCount++
	}

	fun updateCombatant(updatedCombatant: CombatantModel) {
		_currentCombat.value = _currentCombat.value.map {
			if (it.id == updatedCombatant.id) {
				if (it.name != updatedCombatant.name) {
					latestName = updatedCombatant.name
				}
				updatedCombatant
			} else {
				it
			}
		}.sortByInitiative()
	}
}
