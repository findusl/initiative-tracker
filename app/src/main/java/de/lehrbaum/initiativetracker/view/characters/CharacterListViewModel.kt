package de.lehrbaum.initiativetracker.view.characters

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.lehrbaum.initiativetracker.bl.CharacterModel
import de.lehrbaum.initiativetracker.extensions.DelegatingViewModel
import de.lehrbaum.initiativetracker.logic.CharacterRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

interface CharacterListViewModel {
	val characters: StateFlow<List<CharacterViewModel>>
	fun onCharacterSelected(characterViewModel: CharacterViewModel)
	fun onAddNewPressed()
}

@HiltViewModel
class CharacterListViewModelImpl @Inject constructor(
	private val characterRepository: CharacterRepository
) : DelegatingViewModel<CharacterListViewModelImpl.Delegate>(), CharacterListViewModel {

	private val characterModels = characterRepository.characters.map { list ->
		list.associateBy { it.id }
	}.stateIn(viewModelScope, SharingStarted.Eagerly, emptyMap())

	override val characters = characterRepository.characters.map { list ->
		list.map { it.toCharacterViewModel() }
	}.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

	override fun onCharacterSelected(characterViewModel: CharacterViewModel) {
		val model = characterModels.value[characterViewModel.id]
			?: throw NoSuchElementException("No character model with id ${characterViewModel.id} and name ${characterViewModel.name}")
		delegate?.editCharacter(model)
	}

	override fun onAddNewPressed() {
		val model = characterRepository.addCharacter()
		delegate?.editCharacter(model)
	}

	interface Delegate {
		fun editCharacter(characterModel: CharacterModel)
	}

}
