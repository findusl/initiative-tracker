package de.lehrbaum.initiativetracker.ui.shared

import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import de.lehrbaum.initiativetracker.bl.MonsterCache
import de.lehrbaum.initiativetracker.dtos.CombatantId
import de.lehrbaum.initiativetracker.dtos.CombatantModel
import de.lehrbaum.initiativetracker.dtos.UserId
import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import io.ktor.http.Url

@Stable
data class CombatantViewModel(
	val ownerId: UserId,
	val id: CombatantId,
	val name: String,
	val creatureType: String?,
	val initiative: Int?,
	val maxHp: Int?,
	val currentHp: Int?,
	val disabled: Boolean,
	val isHidden: Boolean,
	val active: Boolean = false,
	val isOwned: Boolean = false,
) {

	val initiativeString: String = initiative?.toString() ?: "-"

	val healthPercentage: Double? = if (currentHp != null && maxHp != null) currentHp / maxHp.toDouble() else null

	/* This is a derived state since the monsters could still be loading. */
	val monsterDTO by derivedStateOf { creatureType?.let { MonsterCache.getMonsterByName(it) } }

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

fun CombatantModel.toCombatantViewModel(thisUser: UserId, active: Boolean = false): CombatantViewModel {
	return CombatantViewModel(this.ownerId, id, name, creatureType, initiative, maxHp, currentHp,
		disabled, isHidden, active, this.ownerId == thisUser)
}
