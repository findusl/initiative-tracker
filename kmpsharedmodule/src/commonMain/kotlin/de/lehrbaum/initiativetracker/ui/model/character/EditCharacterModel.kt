package de.lehrbaum.initiativetracker.ui.model.character

import de.lehrbaum.initiativetracker.bl.model.CharacterModel
import de.lehrbaum.initiativetracker.ui.model.shared.EditField
import de.lehrbaum.initiativetracker.ui.model.shared.EditField.Companion.failure
import kotlin.Result.Companion.success

data class EditCharacterModel(
	private val characterModel: CharacterModel,
	private val onSave: (CharacterModel) -> Unit,
	private val onCancel: () -> Unit,
) {
	private val id = characterModel.id
	val nameEdit = EditField(characterModel.name) { input ->
		if (input.isBlank()) failure() else success(input)
	}
	val initiativeModEdit = EditField(characterModel.initiativeMod) { input ->
		if (input.isBlank()) success(null) else input.toIntOrNull()?.let { success(it) } ?: failure()
	}
	val maxHpEdit = EditField(characterModel.maxHp) { input ->
		if (input.isBlank()) success(null) else input.toIntOrNull()?.let { success(it) } ?: failure()
	}

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