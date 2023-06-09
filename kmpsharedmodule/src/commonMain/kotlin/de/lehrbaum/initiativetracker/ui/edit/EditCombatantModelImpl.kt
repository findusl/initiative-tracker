package de.lehrbaum.initiativetracker.ui.edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import de.lehrbaum.initiativetracker.bl.model.CombatantModel
import de.lehrbaum.initiativetracker.ui.shared.CombatantViewModel
import de.lehrbaum.initiativetracker.ui.shared.EditField
import de.lehrbaum.initiativetracker.ui.shared.EditField.Companion.failedParsing
import kotlin.Result.Companion.success

data class EditCombatantModelImpl(
	private val combatantViewModel: CombatantViewModel,
	private val firstEdit: Boolean,
	private val onSave: suspend (CombatantModel) -> Unit,
	private val onCancel: () -> Unit,
) : EditCombatantModel {
    override val id = combatantViewModel.id
	override val nameEdit =
		EditField(combatantViewModel.name, selectOnFirstFocus = firstEdit) { input ->
			if (input.isBlank()) failedParsing() else success(input)
		}
	override val initiativeEdit = EditField(
		combatantViewModel.initiative,
		parseInput = EditField.OptionalIntParser
	)
	override val maxHpEdit = EditField(
		combatantViewModel.maxHp,
		parseInput = EditField.OptionalIntParser
	)
	override val currentHpEdit = EditField(
		combatantViewModel.currentHp,
		parseInput = EditField.OptionalIntParser
	)
	override var isHidden: Boolean by mutableStateOf(combatantViewModel.isHidden)

	override suspend fun saveCombatant() {
		onSave(CombatantModel(
			combatantViewModel.ownerId,
			id,
			nameEdit.value.getOrThrow(),
			initiativeEdit.value.getOrThrow(),
			maxHpEdit.value.getOrThrow(),
			currentHpEdit.value.getOrThrow(),
			combatantViewModel.disabled,
			isHidden,
		))
    }

    override fun cancel() {
        onCancel()
    }
}
