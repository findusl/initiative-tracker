package de.lehrbaum.initiativetracker.dtos

import kotlinx.serialization.Serializable

@Serializable
data class CombatModel(
	val activeCombatantIndex: Int,
	val combatants: List<CombatantModel>,
)
