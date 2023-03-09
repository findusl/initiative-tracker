package de.lehrbaum.initiativetracker.view.characters

import de.lehrbaum.initiativetracker.extensions.DelegatingViewModel
import de.lehrbaum.initiativetracker.logic.CharacterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface EditCharacterViewModel {
	val characterViewModel: StateFlow<CharacterViewModel>
	val isNameError: StateFlow<Boolean>
	val isHitPointsError: StateFlow<Boolean>
	fun onNameUpdated(name: String)
	fun onInitiativeModUpdated(initiativeMod: String)
	fun onHitPointsUpdated(hitPoints: String)
	fun saveCharacter()
}

class EditCharacterViewModelImpl(
	private val characterId: Long,
	private val characterRepository: CharacterRepository
) : DelegatingViewModel<EditCharacterViewModelImpl.Delegate>(), EditCharacterViewModel {
	private val initialCharacterModel = characterRepository.characters.value.first { it.id == characterId }
	private val _characterViewModel = MutableStateFlow(initialCharacterModel.toCharacterViewModel())
	override val characterViewModel: StateFlow<CharacterViewModel> = _characterViewModel

	private val _isNameError = MutableStateFlow(false)
	override val isNameError: StateFlow<Boolean> = _isNameError

	private val _isHitPointsError = MutableStateFlow(false)
	override val isHitPointsError: StateFlow<Boolean> = _isHitPointsError

	override fun onNameUpdated(name: String) {
		_characterViewModel.value = _characterViewModel.value.copy(name = name)
		if (_isNameError.value && name.isNotBlank()) {
			_isNameError.value = false
		}
	}

	override fun onInitiativeModUpdated(initiativeMod: String) {
		val parsedInitiativeMod = initiativeMod.toIntOrNull() ?: 0
		_characterViewModel.value = _characterViewModel.value.copy(initiativeMod = parsedInitiativeMod)
	}

	override fun onHitPointsUpdated(hitPoints: String) {
		val parsedHitPoints = hitPoints.toIntOrNull() ?: 0
		_characterViewModel.value = _characterViewModel.value.copy(hitPoints = parsedHitPoints)
		if (_isHitPointsError.value && parsedHitPoints >= 0) {
			_isHitPointsError.value
		}
	}

	override fun saveCharacter() {
		val character = characterViewModel.value
		if (character.name.isBlank()) {
			_isNameError.value = true
		} else if (character.hitPoints < 0) {
			_isHitPointsError.value = true
		} else {
			characterRepository.updateCharacter(character.toModel())
			delegate?.notifySaved()
			delegate?.popNavigationBackstack()
		}
	}

	interface Delegate {
		fun notifySaved()
		fun popNavigationBackstack()
	}
}
