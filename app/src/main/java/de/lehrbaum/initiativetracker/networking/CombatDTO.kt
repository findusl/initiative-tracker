package de.lehrbaum.initiativetracker.networking

import de.lehrbaum.initiativetracker.logic.CombatantModel
import kotlinx.serialization.Serializable

@Serializable
data class CombatDTO(
	private val activeCombatantIndex: Int,
	private val combatants: List<CombatantDTO>,
)

@Serializable
data class CombatantDTO(
	val id: Long,
	val name: String,
	val initiative: Short,
) {
	constructor(model: CombatantModel) : this(model.id, model.name, model.initiative)
}
