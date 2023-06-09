package de.lehrbaum.initiativetracker.bl

import de.lehrbaum.initiativetracker.bl.data.GeneralSettingsRepository
import de.lehrbaum.initiativetracker.bl.model.CombatantModel
import de.lehrbaum.initiativetracker.bl.model.sortByInitiative
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

private const val DEFAULT_COMBATANT_TITLE = "New"

/**
 * Not thread safe! Has to be called from a single thread.
 */
class CombatController {
	private var nextId = 0L

	/**
	 * The most recent name set on a combatant. Default for new combatants, as often monsters have the same name.
	 */
	// (Should this be in ViewModel rather? It seems like a user helping feature not a logical feature of combat)
	private var latestName: String? = null

	private var combatantCount = 0

	private val _combatants = MutableStateFlow(emptyList<CombatantModel>())
	val combatants: StateFlow<List<CombatantModel>>
		get() = _combatants

	private val _activeCombatantIndex = MutableStateFlow(0)
	val activeCombatantIndex: StateFlow<Int>
		get() = _activeCombatantIndex

	private val ownerId = GeneralSettingsRepository.installationId

	fun nextTurn() {
		val startingIndex = activeCombatantIndex.value
		var newIndex = (startingIndex + 1) % combatantCount
		while (combatants.value[newIndex].disabled && newIndex != startingIndex) {
			newIndex = (newIndex + 1) % combatantCount
		}
		_activeCombatantIndex.value = newIndex
	}

	fun prevTurn() {
		val startingIndex = activeCombatantIndex.value
		var newIndex = startingIndex.decreaseIndex()
		while (combatants.value[newIndex].disabled && newIndex != startingIndex) {
			newIndex = newIndex.decreaseIndex()
		}
		_activeCombatantIndex.value = newIndex
	}

	private fun Int.decreaseIndex(): Int {
		var newActiveCombatant = this - 1
		if (newActiveCombatant < 0) newActiveCombatant = combatantCount - 1
		return newActiveCombatant
	}

	fun addCombatant(
		name: String = latestName ?: DEFAULT_COMBATANT_TITLE,
		initiative: Int? = null // Sorts it to the bottom where the add button is.
	): CombatantModel {
		val newCombatant = CombatantModel(ownerId, nextId++, name, initiative)
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

	fun damageCombatant(id: Long, damage: Int) {
		_combatants.updateCombatant(id) { combatantModel ->
			combatantModel.copy(currentHp = combatantModel.currentHp?.minus(damage))
		}
	}

	fun updateCombatant(updatedCombatant: CombatantModel) {
		_combatants.updateCombatant(updatedCombatant.id, reSort = true) { combatantModel ->
			if (combatantModel.name != updatedCombatant.name) {
				latestName = updatedCombatant.name
			}
			updatedCombatant
		}
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
		if (activeCombatantIndex.value >= combatantCount) {
			_activeCombatantIndex.value = combatantCount - 1
		}
		return oldCombatant
	}

	fun disableCombatant(id: Long) {
		_combatants.updateCombatant(id) { combatantModel ->
			combatantModel.copy(disabled = true)
		}
	}

	fun enableCombatant(id: Long) {
		_combatants.updateCombatant(id) { combatantModel ->
			combatantModel.copy(disabled = false)
		}
	}

	fun jumpToCombatant(id: Long) {
		_activeCombatantIndex.value = combatants.value.indexOfFirst { it.id == id }
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

private inline fun MutableStateFlow<List<CombatantModel>>.updateCombatant(
	id: Long,
	reSort: Boolean = false,
	updater: (CombatantModel) -> CombatantModel
) {
	var result = value.map {
		if (it.id == id) {
			updater(it)
		} else it
	}
	if (reSort) result = result.sortByInitiative()
	this.value = result
}
