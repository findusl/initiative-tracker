package de.lehrbaum.initiativetracker.ui.model.character

import androidx.compose.runtime.State
import kotlinx.coroutines.flow.Flow

interface CharacterListModel {
	val characters: Flow<List<CharacterViewModel>>
	val editCharacterModel: State<EditCharacterModel?>
	fun editCharacter(characterViewModel: CharacterViewModel)
	fun deleteCharacter(characterViewModel: CharacterViewModel)
	fun addNewCharacter()
}