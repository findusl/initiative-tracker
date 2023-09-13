package de.lehrbaum.initiativetracker.ui.character

import androidx.compose.runtime.mutableStateOf
import de.lehrbaum.initiativetracker.bl.data.CharacterRepository
import de.lehrbaum.initiativetracker.bl.model.CharacterModel
import kotlinx.coroutines.flow.map

class CharacterListViewModel {
	private val characterRepository = CharacterRepository()

	val characters = characterRepository.characters.map { list ->
		list.map { it.toCharacterViewModel() }
	}

	val editCharacterModel = mutableStateOf<EditCharacterModel?>(null)

	fun editCharacter(characterViewModel: CharacterViewModel) {
		val model = characterRepository.getbyId(characterViewModel.id)
		editCharacter(model)
	}

	fun deleteCharacter(characterViewModel: CharacterViewModel) {
		characterRepository.removeCharacter(characterViewModel.id)
	}

	fun addNewCharacter() {
		val model = characterRepository.addCharacter()
		editCharacter(model, true)
	}

	private fun editCharacter(characterModel: CharacterModel, firstEdit: Boolean = false) {
		editCharacterModel.value = EditCharacterModel(
			characterModel,
			firstEdit,
			onSave = {
				characterRepository.updateCharacter(it)
				editCharacterModel.value = null
			},
			onCancel = { editCharacterModel.value = null }
		)
	}
}
