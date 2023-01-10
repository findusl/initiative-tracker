package de.lehrbaum.initiativetracker.view.combat.characters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.lehrbaum.initiativetracker.logic.CharacterRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

interface CharacterListViewModel {
	val characters: StateFlow<List<CharacterViewModel>>
}

@HiltViewModel
class CharacterListViewModelImpl @Inject constructor(
	private val characterRepository: CharacterRepository
) : ViewModel(), CharacterListViewModel {

	override val characters = characterRepository.characters.map { list ->
		list.map { CharacterViewModel(it.id, it.name) }
	}.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
}
