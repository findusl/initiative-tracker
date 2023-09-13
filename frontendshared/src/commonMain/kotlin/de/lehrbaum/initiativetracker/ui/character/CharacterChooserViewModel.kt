package de.lehrbaum.initiativetracker.ui.character

import de.lehrbaum.initiativetracker.bl.data.CharacterRepository
import de.lehrbaum.initiativetracker.bl.model.CharacterModel
import kotlinx.coroutines.flow.map

class CharacterChooserViewModel(
	private val onChosen: (CharacterModel, Int, Int) -> Unit,
	private val onCancel: () -> Unit,
) {
	private val characterRepository = CharacterRepository()

	val characters = characterRepository.characters.map { list ->
		list.map { it.toCharacterViewModel() }
	}

	fun cancel() = onCancel()

	fun onChosen(characterViewModel: CharacterViewModel, initiative: Int, currentHp: Int) {
		val model = characterRepository.getById(characterViewModel.id)
		onChosen(model, initiative, currentHp)
	}
}