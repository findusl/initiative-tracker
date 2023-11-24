package de.lehrbaum.initiativetracker.ui.composables

import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import de.lehrbaum.initiativetracker.bl.MonsterCache
import de.lehrbaum.initiativetracker.dtos.CombatantId
import de.lehrbaum.initiativetracker.dtos.CombatantModel
import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import io.ktor.http.Url

/**
 * This has some extra fields necessary for displaying the combatant.
 * Maybe it would be cleaner to copy the fields that are used by the List, instead of having the list think about
 * which fields are part of the viewModel and which fields are part of the actual combatant. But that is extra work
 * of keeping double fields.
 */
@Stable
data class CombatantListViewModel(
	val combatant: CombatantModel,
	val active: Boolean,
	val isOwned: Boolean,
	val id: CombatantId = combatant.id,
	val disabled: Boolean = combatant.disabled,
) {
	val initiativeString: String = combatant.initiative?.toString() ?: "-"

	val healthPercentage: Double? = combatant.run { maxHp?.toDouble()?.let { currentHp?.div(it) } }

	/* This is a derived state since the monsters could still be loading. */
	val monsterDTO by derivedStateOf { combatant.creatureType?.let { MonsterCache.getMonsterByName(it) } }

	fun imageUrl(): Url? {
		val monster = monsterDTO ?: return null
		if (!monster.hasToken) return null
		// like https://5e.tools/img/MM/Air%20Elemental.png
		return URLBuilder(
			protocol = URLProtocol.HTTPS,
			host = "5e.tools",
			pathSegments = listOf("img", monster.source, monster.name + ".png")
		).build()
	}
}
