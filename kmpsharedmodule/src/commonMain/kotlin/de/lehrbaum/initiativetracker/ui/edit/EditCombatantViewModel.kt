package de.lehrbaum.initiativetracker.ui.edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import de.lehrbaum.initiativetracker.dtos.CombatantModel
import de.lehrbaum.initiativetracker.ui.shared.CombatantViewModel
import de.lehrbaum.initiativetracker.ui.shared.EditFieldViewModel

data class EditCombatantViewModel(
	private val combatantViewModel: CombatantViewModel,
	private val firstEdit: Boolean,
	private val onSave: suspend (CombatantModel) -> Unit,
	private val onCancel: () -> Unit,
) {
	val id = combatantViewModel.id
	val nameEdit = EditFieldViewModel(
		combatantViewModel.name,
		selectOnFirstFocus = firstEdit,
		parseInput = EditFieldViewModel.RequiredStringParser
	)
	val initiativeEdit = EditFieldViewModel(
		combatantViewModel.initiative,
		parseInput = EditFieldViewModel.OptionalIntParser
	)
	val maxHpEdit = EditFieldViewModel(
		combatantViewModel.maxHp,
		parseInput = EditFieldViewModel.OptionalIntParser
	)
	val currentHpEdit = EditFieldViewModel(
		combatantViewModel.currentHp,
		parseInput = EditFieldViewModel.OptionalIntParser
	)
	var isHidden: Boolean by mutableStateOf(combatantViewModel.isHidden)

	suspend fun saveCombatant() {
		onSave(CombatantModel(
			combatantViewModel.ownerId,
			id,
			nameEdit.value.getOrThrow(),
			initiativeEdit.value.getOrThrow(), // TODO implement
			maxHpEdit.value.getOrThrow(),
			currentHpEdit.value.getOrThrow(),
			null,
			combatantViewModel.disabled,
			isHidden,
		))
    }

    fun cancel() {
        onCancel()
    }
}
