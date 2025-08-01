package de.lehrbaum.initiativetracker.ui.character

import androidx.compose.runtime.Stable
import de.lehrbaum.initiativetracker.bl.model.CharacterModel
import de.lehrbaum.initiativetracker.data.CharacterRepository
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.map

@Stable
data class CharacterChooserViewModel(
	private val onChosen: (CharacterModel, Int, Int) -> Unit,
	private val onCancel: () -> Unit,
) {
	private val characterRepository = CharacterRepository()

	val characters = characterRepository.characters.map { list ->
		list.map { it.toCharacterViewModel() }.toImmutableList()
	}

	fun cancel() = onCancel()

	fun onChosen(
		characterViewModel: CharacterViewModel,
		initiative: Int,
		currentHp: Int,
	) {
		val model = characterRepository.getById(characterViewModel.id)
		onChosen(model, initiative, currentHp)
	}
}
