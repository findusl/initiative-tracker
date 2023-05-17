package de.lehrbaum.initiativetracker.bl

import de.lehrbaum.initiativetracker.bl.model.CombatantModel
import de.lehrbaum.initiativetracker.bl.model.sortByInitiative
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

private const val DEFAULT_COMBATANT_TITLE = "New Combatant"

class CombatController {
	private var nextId = 0L

	/**
	 * The most recent name set on a combatant. Default for new combatants, as often monsters have the same name.
	 * (Should this be in ViewModel rather? It seems like a user helping feature not a logical feature of combat)
	 */
	private var latestName: String? = null

	private var combatantCount = 0

	private val _combatants = MutableStateFlow(emptyList<CombatantModel>())
	val combatants: StateFlow<List<CombatantModel>>
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
		initiative: Int = -9 // Sorts it to the bottom where the add button is.
	): CombatantModel {
		val newCombatant = CombatantModel(nextId++, name, initiative, 0, 0)
		_combatants.value = (_combatants.value + newCombatant).sortByInitiative()
		combatantCount++
		return newCombatant
	}

	fun addCombatant(combatantModel: CombatantModel): CombatantModel {
		val newCombatant = combatantModel.copy(id = nextId++)
		_combatants.value = (_combatants.value + newCombatant).sortByInitiative()
		combatantCount++
		return newCombatant
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

	fun deleteCombatant(id: Long): CombatantModel? {
		var oldCombatant: CombatantModel? = null
		_combatants.value = _combatants.value
			.filter { combatantModel ->
				if (combatantModel.id == id) {
					oldCombatant = combatantModel
					false
				} else true
			}
		if (oldCombatant != null) {
			combatantCount--
		}
		return oldCombatant
	}

	/**
	 * This is not a clean solution. Better would be to create a whole new CombatController with the state.
	 * But that doesn't fit into the current architecture and I'm not yet sure how to implement the best solution.
	 */
	fun overwriteWithExistingCombat(combatants: List<CombatantModel>, activeCombatantIndex: Int) {
		combatantCount = combatants.size
		_combatants.value = combatants.toList()
		nextId = combatants.maxOfOrNull { it.id }?.inc() ?: 0
		_activeCombatantIndex.value = activeCombatantIndex
	}
}
