package de.lehrbaum.initiativetracker.ui.character

import de.lehrbaum.initiativetracker.bl.model.CharacterModel
import de.lehrbaum.initiativetracker.ui.shared.EditFieldViewModel
import de.lehrbaum.initiativetracker.ui.shared.EditFieldViewModel.Companion.OptionalIntParser
import de.lehrbaum.initiativetracker.ui.shared.EditFieldViewModel.Companion.failedParsing
import kotlin.Result.Companion.success

data class EditCharacterModel(
	private val characterModel: CharacterModel,
	private val firstEdit: Boolean,
	private val onSave: (CharacterModel) -> Unit,
	private val onCancel: () -> Unit,
) {
	private val id = characterModel.id
	val nameEdit = EditFieldViewModel(characterModel.name, selectOnFirstFocus = firstEdit) { input ->
		if (input.isBlank()) failedParsing() else success(input)
	}
	val initiativeModEdit = EditFieldViewModel(characterModel.initiativeMod, parseInput = OptionalIntParser)
	val maxHpEdit = EditFieldViewModel(characterModel.maxHp, parseInput = OptionalIntParser)

	fun saveCharacter() {
		onSave(CharacterModel(
			id,
			nameEdit.value.getOrThrow(),
			initiativeModEdit.value.getOrThrow(),
			maxHpEdit.value.getOrThrow(),
		))
	}

	fun cancel() {
		onCancel()
	}
}