package de.lehrbaum.initiativetracker.logic

import androidx.annotation.MainThread
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

private const val DEFAULT_COMBATANT_TITLE = "New Combatant"

@MainThread
class CombatController {
	private var nextId = 0L

	/**
	 * The most recent name set on a combatant. Default for new combatants, as often monsters have the same name.
	 * (Should this be in ViewModel rather? It seems like a user helping feature not a logical feature of combat)
	 */
	private var latestName: String? = null

	private var combatantCount = 0

	private val _combatants = MutableStateFlow(emptySequence<CombatantModel>())
	val combatants: StateFlow<Sequence<CombatantModel>>
		get() = _combatants

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

	fun addCombatant(
		name: String = latestName ?: DEFAULT_COMBATANT_TITLE,
		initiative: Short = -99
	) {
		val newCombatant = CombatantModel(nextId++, name, initiative)
		_combatants.value = (_combatants.value + newCombatant).sortByInitiative()
		combatantCount++
	}

	fun updateCombatant(updatedCombatant: CombatantModel) {
		_combatants.value = _combatants.value.map {
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

	fun deleteCombatant(position: Int): CombatantModel {
		var oldCombatant: CombatantModel? = null
		_combatants.value = _combatants.value
			.filterIndexed { index, combatantModel ->
				if (index == position) {
					oldCombatant = combatantModel
					false
				} else true
			}
		return oldCombatant!!
	}
}
