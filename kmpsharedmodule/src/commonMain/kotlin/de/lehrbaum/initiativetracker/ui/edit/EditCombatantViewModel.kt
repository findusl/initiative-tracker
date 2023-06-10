package de.lehrbaum.initiativetracker.ui.edit

import androidx.compose.runtime.Stable
import de.lehrbaum.initiativetracker.ui.shared.EditField

@Stable
interface EditCombatantViewModel {
	val id: Long
	val nameEdit: EditField<String>
	val initiativeEdit: EditField<Int?>
	val maxHpEdit: EditField<Int?>
	val currentHpEdit: EditField<Int?>
	var isHidden: Boolean

    suspend fun saveCombatant()

    fun cancel()
}
