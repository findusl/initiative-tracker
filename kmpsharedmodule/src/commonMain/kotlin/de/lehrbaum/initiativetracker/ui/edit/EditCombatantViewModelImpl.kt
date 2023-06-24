package de.lehrbaum.initiativetracker.ui.edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import de.lehrbaum.initiativetracker.dtos.CombatantModel
import de.lehrbaum.initiativetracker.ui.shared.CombatantViewModel
import de.lehrbaum.initiativetracker.ui.shared.EditFieldViewModel
import de.lehrbaum.initiativetracker.ui.shared.EditFieldViewModel.Companion.failedParsing
import kotlin.Result.Companion.success

data class EditCombatantViewModelImpl(
	private val combatantViewModel: CombatantViewModel,
	private val firstEdit: Boolean,
	private val onSave: suspend (CombatantModel) -> Unit,
	private val onCancel: () -> Unit,
) : EditCombatantViewModel {
    override val id = combatantViewModel.id
	override val nameEdit =
		EditFieldViewModel(combatantViewModel.name, selectOnFirstFocus = firstEdit) { input ->
			if (input.isBlank()) failedParsing() else success(input)
		}
	override val initiativeEdit = EditFieldViewModel(
		combatantViewModel.initiative,
		parseInput = EditFieldViewModel.OptionalIntParser
	)
	override val maxHpEdit = EditFieldViewModel(
		combatantViewModel.maxHp,
		parseInput = EditFieldViewModel.OptionalIntParser
	)
	override val currentHpEdit = EditFieldViewModel(
		combatantViewModel.currentHp,
		parseInput = EditFieldViewModel.OptionalIntParser
	)
	override var isHidden: Boolean by mutableStateOf(combatantViewModel.isHidden)

	override suspend fun saveCombatant() {
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

    override fun cancel() {
        onCancel()
    }
}
