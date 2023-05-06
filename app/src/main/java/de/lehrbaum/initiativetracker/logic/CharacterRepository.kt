package de.lehrbaum.initiativetracker.logic

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import de.lehrbaum.initiativetracker.bl.model.CharacterModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

private const val PREFERENCE_NAME = "CHARACTER_PREFERENCES"
private const val CHARACTERS_KEY = "CHARACTERS"

private val JsonMapper = Json { ignoreUnknownKeys = true }

@Singleton
class CharacterRepository @Inject constructor(
	@ApplicationContext context: Context
) {
	private val sharedPrefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

	private val _characters = MutableStateFlow(loadCharacters())
	val characters: StateFlow<List<CharacterModel>> = _characters

	private fun loadCharacters(): List<CharacterModel> {
		val characterJsonSet = sharedPrefs.getStringSet(CHARACTERS_KEY, emptySet())!!
		return characterJsonSet.asSequence()
			.map { JsonMapper.decodeFromString<CharacterModel>(it) }
			.toList()
	}

	private fun saveCharacterList() {
		val characterJsonSet = _characters.value.asSequence()
			.map { JsonMapper.encodeToString(it) }
			.toSet()
		sharedPrefs.edit {
			this.putStringSet(CHARACTERS_KEY, characterJsonSet)
		}
	}

	fun addCharacter(): CharacterModel {
		val defaultCharacter = CharacterModel(nextFreeId(), "New Character", 0, 0)
		_characters.value = _characters.value + defaultCharacter
		saveCharacterList()
		return defaultCharacter
	}

	fun updateCharacter(character: CharacterModel) {
		_characters.value = _characters.value.map {
			if (it.id == character.id) character else it
		}
		saveCharacterList()
	}

	fun removeCharacter(character: CharacterModel) {
		_characters.value = _characters.value.filter { it.id != character.id }
		saveCharacterList()
	}

	private fun nextFreeId(): Long {
		return characters.value.maxOfOrNull { it.id }?.inc() ?: 0
	}
}
