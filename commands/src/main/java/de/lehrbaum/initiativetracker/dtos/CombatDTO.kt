package de.lehrbaum.initiativetracker.dtos

import kotlinx.serialization.Serializable

@Serializable
data class CombatDTO(
	val activeCombatantIndex: Int,
	val combatants: List<CombatantDTO>,
)

@Serializable
data class CombatantDTO(
	val id: Long,
	val name: String,
	val initiative: Int?,
	val maxHp: Int?,
	val currentHp: Int?,
	val disabled: Boolean,
	val isHidden: Boolean,
)
