package de.lehrbaum.initiativetracker.ui.character

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import de.lehrbaum.initiativetracker.data.CharacterRepository
import de.lehrbaum.initiativetracker.bl.model.CharacterModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.map

@Stable
data class CharacterListViewModel(
	private val characterRepository: CharacterRepository = CharacterRepository()
) {

	val characters = characterRepository.characters.map { list ->
		list.map { it.toCharacterViewModel() }.toImmutableList()
	}

	val editCharacterModel = mutableStateOf<EditCharacterModel?>(null)

	fun editCharacter(characterViewModel: CharacterViewModel) {
		val model = characterRepository.getById(characterViewModel.id)
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
