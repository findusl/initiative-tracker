package de.lehrbaum.initiativetracker.view.combat.characters

import androidx.lifecycle.ViewModel
import de.lehrbaum.initiativetracker.logic.CharacterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface EditCharacterViewModel {
	val characterViewModel: StateFlow<CharacterViewModel>
	val isNameError: StateFlow<Boolean>
	fun onNameUpdated(name: String)
	fun onInitiativeModUpdated(initiativeMod: Int)
	fun onHitPointsUpdated(hitPoints: Int)
	fun saveCharacter()
}

class EditCharacterViewModelImpl(
	private val characterId: Long,
	private val characterRepository: CharacterRepository
) : ViewModel(), EditCharacterViewModel {
	private val initialCharacterModel = characterRepository.characters.value.first { it.id == characterId }
	private val _characterViewModel = MutableStateFlow(initialCharacterModel.toCharacterViewModel())
	override val characterViewModel: StateFlow<CharacterViewModel> = _characterViewModel

	private val _isNameError = MutableStateFlow(false)
	override val isNameError: StateFlow<Boolean> = _isNameError

	override fun onNameUpdated(name: String) {
		val newCharacterViewModel = _characterViewModel.value.copy(name = name)
		_characterViewModel.value = newCharacterViewModel
		if (_isNameError.value) {
			_isNameError.value = false
		}
	}

	override fun onInitiativeModUpdated(initiativeMod: Int) {
		_characterViewModel.value = _characterViewModel.value.copy(initiativeMod = initiativeMod)
	}

	override fun onHitPointsUpdated(hitPoints: Int) {
		_characterViewModel.value = _characterViewModel.value.copy(hitPoints = hitPoints)
	}

	override fun saveCharacter() {
		val character = characterViewModel.value
		if (character.name.isBlank()) {
			_isNameError.value = true
		} else {
			characterRepository.updateCharacter(character.toModel())
		}
	}
}
