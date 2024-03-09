package de.lehrbaum.initiativetracker.bl

import de.lehrbaum.initiativetracker.bl.DamageDecision.DOUBLE
import de.lehrbaum.initiativetracker.bl.DamageDecision.FULL
import de.lehrbaum.initiativetracker.bl.DamageDecision.HALF
import de.lehrbaum.initiativetracker.bl.DamageDecision.NONE
import de.lehrbaum.initiativetracker.data.GeneralSettingsRepository
import de.lehrbaum.initiativetracker.bl.model.AoeOptions
import de.lehrbaum.initiativetracker.bl.model.SavingThrow
import de.lehrbaum.initiativetracker.dtos.CombatantId
import de.lehrbaum.initiativetracker.dtos.CombatantModel
import de.lehrbaum.initiativetracker.dtos.UserId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.random.Random

/**
 * Not thread safe! Has to be called from main thread.
 */
class CombatController(
	generalSettingsRepository: GeneralSettingsRepository,
	private val confirmationRequester: ConfirmationRequester,
	seed: Long? = null
) {
	private val random = seed?.let { Random(seed) } ?: Random
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
		if (combatantCount == 0) return
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
		val probableSourceName = determineSourceName(sourceId)

		confirmationRequester.confirmDamage(damage, target, probableSourceName)?.let { decision ->
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

	fun handleFinishTurnRequest(activeCombatantIndex: Int): Boolean {
		if (activeCombatantIndex == this.activeCombatantIndex.value) {
			nextTurn()
			return true
		}
		return false
	}

	fun damageCombatant(targetId: CombatantId, damage: Int) {
		if (damage == 0) return
		_combatants.updateCombatant(targetId) { combatantModel ->
			combatantModel.copy(currentHp = combatantModel.currentHp?.minus(damage))
		}
	}

	suspend fun handleAoeRequest(
		aoeOptions: AoeOptions,
		targetIds: List<CombatantId>,
		sourceId: UserId,
	): Boolean {
		val preliminaryResults = targetIds
			.mapNotNull { combatantId ->
				combatants.value.firstOrNull { it.id == combatantId }
			}.associateWith { combatant ->
				calculatePreliminaryResult(aoeOptions, combatant)
			}
		val probableSourceName = determineSourceName(sourceId)

		val decisions = confirmationRequester.confirmAoe(aoeOptions, preliminaryResults, probableSourceName) ?: return false
		val results = decisions.mapNotNull { (combatant, decision) ->
			val result = when (decision) {
				AOEDecision.KEEP -> preliminaryResults[combatant] as? AOEResult
				AOEDecision.OVERWRITE_SUCCESS -> AOEResult.SUCCESS(null)
				AOEDecision.OVERWRITE_FAILURE -> AOEResult.FAILURE(null)
				AOEDecision.OVERWRITE_IGNORE -> null
			}
			result?.let { Pair(combatant, result) }
		}
		results.forEach { (combatant, result) ->
			if (result is AOEResult.FAILURE)
				damageCombatant(combatant.id, aoeOptions.damage)
			else if (result is AOEResult.SUCCESS && aoeOptions.halfOnFailure)
				damageCombatant(combatant.id, aoeOptions.damage / 2)
		}
		return true
	}

	private fun calculatePreliminaryResult(aoeOptions: AoeOptions, combatant: CombatantModel): PreliminaryAOEResult {
		if (aoeOptions.save == null) {
			return AOEResult.SUCCESS(null)
		} else {
			val (type, dc) = aoeOptions.save
			val bonus = combatant.saveBonus(type)
			return if (bonus == null)
				PreliminaryAOEResult.INDETERMINATE
			else {
				val roll = random.d20() + bonus
				if (roll >= dc) AOEResult.SUCCESS(roll) else AOEResult.FAILURE(roll)
			}
		}
	}

	private fun CombatantModel.saveBonus(type: SavingThrow): Int? =
		when (type) {
			SavingThrow.STR -> strSave
			SavingThrow.DEX -> dexSave
			SavingThrow.CON -> conSave
			SavingThrow.INT -> intSave
			SavingThrow.WIS -> wisSave
			SavingThrow.CHA -> chaSave
		}

	private fun determineSourceName(sourceId: UserId): String? {
		if (sourceId == hostId) return "Host"
		// I don't have a name of the player, so I take the first combatant they control that is not a creature
		// I just hope that's their main character
		return combatants.value
			.firstOrNull { it.ownerId == sourceId && it.creatureType == null }?.name
	}

	fun updateCombatant(updatedCombatant: CombatantModel) {
		_combatants.updateCombatant(updatedCombatant.id, reSort = true) {
			updatedCombatant
		}
	}

	fun deleteCombatant(id: CombatantId): CombatantModel? {
		var oldCombatant: CombatantModel? = null
		var oldIndex: Int? = null
		_combatants.value = _combatants.value
			.filterIndexed { index, combatantModel ->
				if (combatantModel.id == id) {
					oldCombatant = combatantModel
					oldIndex = index
					false
				} else true
			}
		if (oldCombatant != null) {
			combatantCount--
		}
		if (oldIndex != null && activeCombatantIndex.value > oldIndex!! || activeCombatantIndex.value >= combatantCount) {
			_activeCombatantIndex.value--
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
	suspend fun confirmAoe(
		aoeOptions: AoeOptions,
		targetRolls: Map<CombatantModel, PreliminaryAOEResult>,
		probableSource: String?
	): Map<CombatantModel, AOEDecision>?
}

enum class DamageDecision {
	FULL, HALF, DOUBLE, NONE
}

sealed interface PreliminaryAOEResult {
	data object INDETERMINATE : PreliminaryAOEResult
}

sealed interface AOEResult : PreliminaryAOEResult {
	val rollSum: Int?

	data class SUCCESS(override val rollSum: Int?) : AOEResult

	data class FAILURE(override val rollSum: Int?) : AOEResult
}

enum class AOEDecision {
	KEEP, OVERWRITE_SUCCESS, OVERWRITE_FAILURE, OVERWRITE_IGNORE
}
