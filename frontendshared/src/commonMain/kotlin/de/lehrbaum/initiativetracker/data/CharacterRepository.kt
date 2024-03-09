package de.lehrbaum.initiativetracker.data

import com.russhwolf.settings.ExperimentalSettingsApi
import de.lehrbaum.initiativetracker.bl.model.CharacterModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.ExperimentalSerializationApi

private const val SETTINGS_NAME = "CHARACTER_PREFERENCES"

private const val SETTINGS_KEY = "CHARACTERS"

@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
class CharacterRepository {
	private val settings = createSettingsFactory().create(SETTINGS_NAME)

	val characters = MutableStateFlow(loadCharacters())

	private fun loadCharacters(): List<CharacterModel> =
		settings.decodeValue(SETTINGS_KEY, emptyList())

	private fun persistCharacters() =
		settings.encodeValue(SETTINGS_KEY, characters.value)

	fun addCharacter(): CharacterModel {
		val defaultCharacter = CharacterModel(nextFreeId(), "New Character", initiativeMod = null, maxHp = null)
		characters.value += defaultCharacter
		persistCharacters()
		return defaultCharacter
	}

	fun updateCharacter(character: CharacterModel) {
		characters.value = characters.value.map {
			if (it.id == character.id) character else it
		}
		persistCharacters()
	}

	fun removeCharacter(id: Long) {
		characters.value = characters.value.filter { it.id != id }
		persistCharacters()
	}

	fun getById(id: Long): CharacterModel = characters.value.first { it.id == id }

	private fun nextFreeId(): Long {
		return characters.value.maxOfOrNull { it.id }?.inc() ?: 0
	}
}