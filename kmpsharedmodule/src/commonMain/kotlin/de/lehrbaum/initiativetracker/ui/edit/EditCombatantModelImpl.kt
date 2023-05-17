package de.lehrbaum.initiativetracker.ui.edit

import de.lehrbaum.initiativetracker.bl.model.CombatantModel
import de.lehrbaum.initiativetracker.ui.shared.CombatantViewModel
import de.lehrbaum.initiativetracker.ui.shared.EditField
import de.lehrbaum.initiativetracker.ui.shared.EditField.Companion.failure
import kotlin.Result.Companion.success

data class EditCombatantModelImpl(
	private val combatantViewModel: CombatantViewModel,
	private val onSave: (CombatantModel) -> Unit,
	private val onCancel: () -> Unit,
) : EditCombatantModel {
    private val id = combatantViewModel.id
	override val nameEdit = EditField(combatantViewModel.name) { input ->
		if (input.isBlank()) failure() else success(input)
	}
	override val initiativeEdit = EditField(combatantViewModel.initiative, parseInput = EditField.RequiredIntParser)
	override val maxHpEdit = EditField(combatantViewModel.maxHp, parseInput = EditField.RequiredIntParser)
	override val currentHpEdit = EditField(combatantViewModel.currentHp, parseInput = EditField.RequiredIntParser)

	override fun saveCombatant() {
		onSave(CombatantModel(
			id,
			nameEdit.value.getOrThrow(),
			initiativeEdit.value.getOrThrow(),
			maxHpEdit.value.getOrThrow(),
			currentHpEdit.value.getOrThrow()
		))
    }

    override fun cancel() {
        onCancel()
    }
}
