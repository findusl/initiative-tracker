package de.lehrbaum.initiativetracker.commands

import de.lehrbaum.initiativetracker.dtos.CombatantDTO
import kotlinx.serialization.Serializable

@Serializable
sealed interface ServerToHostCommand {

	@Serializable
	data class AddCombatant(val combatant: CombatantDTO) : ServerToHostCommand

	@Serializable
	data class EditCombatant(val combatant: CombatantDTO) : ServerToHostCommand

	@Serializable
	data class DamageCombatant(val combatantId: Long, val damage: Int, val ownerId: Long) : ServerToHostCommand
}