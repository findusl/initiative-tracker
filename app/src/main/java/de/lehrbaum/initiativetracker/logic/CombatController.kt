package de.lehrbaum.initiativetracker.logic

import android.os.Looper
import androidx.annotation.MainThread
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.internal.toImmutableList

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

	fun deleteCombatant(position: Int): CombatantModel? {
		var oldCombatant: CombatantModel? = null
		_combatants.value = _combatants.value
			.filterIndexed { index, combatantModel ->
				if (index == position) {
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
		@Suppress("UsePropertyAccessSyntax") // This is clearly a function depending on env not a property
		assert(Looper.getMainLooper().isCurrentThread()) { "This should be called on main thread to avoid race conditions" }

		combatantCount = combatants.size
		_combatants.value = combatants.toImmutableList()
		nextId = combatants.maxOfOrNull { it.id }?.inc() ?: 0
		_activeCombatantIndex.value = activeCombatantIndex
	}
}
