package de.lehrbaum.initiativetracker.commands

import de.lehrbaum.initiativetracker.dtos.CombatantDTO
import kotlinx.serialization.Serializable

@Serializable
sealed interface ServerToHostCommand {

	@Serializable
	data class AddCombatant(val combatant: CombatantDTO) : ServerToHostCommand
}