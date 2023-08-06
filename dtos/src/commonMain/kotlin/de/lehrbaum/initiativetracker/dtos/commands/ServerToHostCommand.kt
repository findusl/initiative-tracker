package de.lehrbaum.initiativetracker.dtos.commands

import de.lehrbaum.initiativetracker.dtos.CombatantModel
import kotlinx.serialization.Serializable

@Serializable
sealed interface ServerToHostCommand {

	@Serializable
	data class AddCombatant(val combatant: CombatantModel) : ServerToHostCommand

	@Serializable
	data class EditCombatant(val combatant: CombatantModel) : ServerToHostCommand

	@Serializable
	data class DamageCombatant(val combatantId: Long, val damage: Int, val ownerId: Long) : ServerToHostCommand
}