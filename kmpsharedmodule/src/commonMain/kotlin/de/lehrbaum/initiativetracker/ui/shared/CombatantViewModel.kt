package de.lehrbaum.initiativetracker.ui.shared

import androidx.compose.runtime.Immutable
import de.lehrbaum.initiativetracker.bl.model.CombatantModel

@Immutable
data class CombatantViewModel(
	val ownerId: Long,
	val id: Long,
	val name: String,
	val initiative: Int?,
	val maxHp: Int?,
	val currentHp: Int?,
	val disabled: Boolean,
	val isHidden: Boolean,
	var active: Boolean = false,
) {

	val initiativeString: String = initiative?.toString() ?: "-"

	val healthPercentage: Double? = if (currentHp != null && maxHp != null) currentHp / maxHp.toDouble() else null
}

fun CombatantModel.toCombatantViewModel(active: Boolean = false): CombatantViewModel {
	return CombatantViewModel(ownerId, id, name, initiative, maxHp, currentHp, disabled, isHidden, active)
}
