package de.lehrbaum.initiativetracker.ui.edit

import androidx.compose.runtime.Stable
import de.lehrbaum.initiativetracker.ui.shared.EditFieldViewModel

@Stable
interface EditCombatantViewModel {
	val id: Long
	val nameEdit: EditFieldViewModel<String>
	val initiativeEdit: EditFieldViewModel<Int?>
	val maxHpEdit: EditFieldViewModel<Int?>
	val currentHpEdit: EditFieldViewModel<Int?>
	var isHidden: Boolean

    suspend fun saveCombatant()

    fun cancel()
}
