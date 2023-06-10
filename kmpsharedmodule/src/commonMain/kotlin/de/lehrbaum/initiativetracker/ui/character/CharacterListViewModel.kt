package de.lehrbaum.initiativetracker.ui.character

import androidx.compose.runtime.State
import kotlinx.coroutines.flow.Flow

interface CharacterListViewModel {
	val characters: Flow<List<CharacterViewModel>>
	val editCharacterModel: State<EditCharacterModel?>
	fun editCharacter(characterViewModel: CharacterViewModel)
	fun deleteCharacter(characterViewModel: CharacterViewModel)
	fun addNewCharacter()
}