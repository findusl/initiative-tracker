package de.lehrbaum.initiativetracker.ui.model.edit

import androidx.compose.runtime.Stable
import de.lehrbaum.initiativetracker.ui.model.shared.EditField

@Stable
interface EditCombatantModel {
	val nameEdit: EditField<String>
	val initiativeEdit: EditField<Int>
	val maxHpEdit: EditField<Int>
	val currentHpEdit: EditField<Int>

    fun saveCombatant()
    fun cancel()
}
