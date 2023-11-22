package de.lehrbaum.initiativetracker.bl

import de.lehrbaum.initiativetracker.bl.DamageDecision.DOUBLE
import de.lehrbaum.initiativetracker.bl.DamageDecision.FULL
import de.lehrbaum.initiativetracker.bl.DamageDecision.HALF
import de.lehrbaum.initiativetracker.bl.DamageDecision.NONE
import de.lehrbaum.initiativetracker.bl.data.GeneralSettingsRepository
import de.lehrbaum.initiativetracker.dtos.CombatantId
import de.lehrbaum.initiativetracker.dtos.CombatantModel
import de.lehrbaum.initiativetracker.dtos.UserId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Not thread safe! Has to be called from main thread.
 */
class CombatController(
	generalSettingsRepository: GeneralSettingsRepository,
	private val confirmationRequester: ConfirmationRequester
) {
	private var nextId = 0L

	private var combatantCount = 0

	private val _combatants = MutableStateFlow(emptyList<CombatantModel>())
	val combatants: StateFlow<List<CombatantModel>>
		get() = _combatants

	private val _activeCombatantIndex = MutableStateFlow(0)
	val activeCombatantIndex: StateFlow<Int>
		get() = _activeCombatantIndex

	val hostId = UserId(generalSettingsRepository.installationId)

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
		name: String = "",
		initiative: Int? = null // Sorts it to the bottom where the add button is.
	): CombatantModel {
		val newCombatant = CombatantModel(hostId, id = CombatantId(nextId++), name, initiative = initiative)
		_combatants.value = (_combatants.value + newCombatant).sortByInitiative()
		combatantCount++
		return newCombatant
	}

	fun addCombatant(combatantModel: CombatantModel): CombatantModel {
		val newCombatant = combatantModel.copy(id = CombatantId(nextId++))
		_combatants.value = (_combatants.value + newCombatant).sortByInitiative()
		combatantCount++
		return newCombatant
	}

	suspend fun handleDamageCombatantRequest(targetId: CombatantId, damage: Int, sourceId: UserId): Boolean {
		val target = combatants.value.first { it.id == targetId }
		if (sourceId == hostId || target.ownerId == sourceId) {
			damageCombatant(target.id, damage)
			return true
		}
		// I don't have a name of the player, so I take the first combatant they control that is not a creature
		// I just hope that's their main character
		val probableSource = combatants.value
			.firstOrNull { it.ownerId == sourceId && it.creatureType == null }

		confirmationRequester.confirmDamage(damage, target, probableSource?.name)?.let { decision ->
			val actualDamage = when (decision) {
				FULL -> damage
				HALF -> damage / 2
				DOUBLE -> damage * 2
				NONE -> 0
			}
			damageCombatant(target.id, actualDamage)
			return true
		} ?: return false
	}

	fun damageCombatant(targetId: CombatantId, damage: Int) {
		if (damage == 0) return
		_combatants.updateCombatant(targetId) { combatantModel ->
			combatantModel.copy(currentHp = combatantModel.currentHp?.minus(damage))
		}
	}

	fun updateCombatant(updatedCombatant: CombatantModel) {
		_combatants.updateCombatant(updatedCombatant.id, reSort = true) {
			updatedCombatant
		}
	}

	fun deleteCombatant(id: CombatantId): CombatantModel? {
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

	fun disableCombatant(id: CombatantId) {
		_combatants.updateCombatant(id) { combatantModel ->
			combatantModel.copy(disabled = true)
		}
	}

	fun enableCombatant(id: CombatantId) {
		_combatants.updateCombatant(id) { combatantModel ->
			combatantModel.copy(disabled = false)
		}
	}

	fun jumpToCombatant(id: CombatantId) {
		_activeCombatantIndex.value = combatants.value.indexOfFirst { it.id == id }
	}

	/**
	 * This seems a dirty solution. Better would be to create a whole new CombatController with the state.
	 * But that doesn't fit into the current architecture, and I'm not sure how to implement the best solution.
	 */
	fun overwriteWithExistingCombat(combatants: List<CombatantModel>, activeCombatantIndex: Int) {
		combatantCount = combatants.size
		_combatants.value = combatants.toList()
		nextId = combatants.maxOfOrNull { it.id.id }?.inc() ?: 0
		_activeCombatantIndex.value = activeCombatantIndex
	}
}

private inline fun MutableStateFlow<List<CombatantModel>>.updateCombatant(
	id: CombatantId,
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

/**
 * Sorts predictably. First by initiative then by id. null initiatives are lower than any other initiatives
 */
private fun Iterable<CombatantModel>.sortByInitiative() =
	sortedWith(compareByDescending(CombatantModel::initiative).thenBy { it.id })

interface ConfirmationRequester {
	suspend fun confirmDamage(damage: Int, target: CombatantModel, probableSource: String?): DamageDecision?
}

enum class DamageDecision {
	FULL, HALF, DOUBLE, NONE
}
