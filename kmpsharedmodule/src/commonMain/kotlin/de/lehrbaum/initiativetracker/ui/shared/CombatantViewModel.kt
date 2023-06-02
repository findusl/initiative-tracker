package de.lehrbaum.initiativetracker.ui.shared

import androidx.compose.runtime.Immutable
import de.lehrbaum.initiativetracker.bl.model.CombatantModel

@Immutable
data class CombatantViewModel(
	val id: Long,
	val name: String,
	val initiative: Int,
	val maxHp: Int,
	val currentHp: Int,
	val disabled: Boolean,
	var active: Boolean = false,
) : Comparable<CombatantViewModel> {

	val initiativeString: String = initiative.toString()

	val healthPercentage = currentHp/maxHp.toDouble()

	override fun compareTo(other: CombatantViewModel): Int {
		var order = initiative - other.initiative
		if (order == 0)
			order = (id - other.id).toInt()
		return order
	}

	fun toCombatantModel(): CombatantModel {
		return CombatantModel(id, name, initiative, maxHp, currentHp, disabled)
	}
}

fun CombatantModel.toCombatantViewModel(active: Boolean = false): CombatantViewModel {
	return CombatantViewModel(id, name, initiative, maxHp, currentHp, disabled, active)
}
