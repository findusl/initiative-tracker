package de.lehrbaum.initiativetracker.dtos

import kotlinx.serialization.Serializable

@Serializable
data class CombatModel(
	val activeCombatantIndex: Int,
	val combatants: List<CombatantModel>,
)

@Serializable
data class CombatantModel(
	val ownerId: UserId,
	val id: CombatantId = CombatantId.UNKNOWN,
	val name: String,
	val initiative: Int? = null,
	val maxHp: Int? = null,
	val currentHp: Int? = null,
	val creatureType: String? = null,
	val disabled: Boolean = false,
	val isHidden: Boolean = false,
)
