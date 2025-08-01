package de.lehrbaum.initiativetracker.bl

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import de.lehrbaum.initiativetracker.networking.bestiary.MonsterDTO

/**
 * Caches a list of known monsters. Has to be initialized by a different class.
 */
object MonsterCache {
	var monsters: List<MonsterDTO> by mutableStateOf(emptyList())
	var monstersByName: Map<String, MonsterDTO>? by mutableStateOf(null)

	fun getMonsterByName(name: String): MonsterDTO? {
		// Fallback while map is not yet loaded. Completely unnecessary optimization
		return monstersByName?.let {
			it.getOrElse(name) { null }
		} ?: monsters.firstOrNull { it.displayName == name }
	}
}
