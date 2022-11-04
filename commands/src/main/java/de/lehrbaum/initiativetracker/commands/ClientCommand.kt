package de.lehrbaum.initiativetracker.commands

import de.lehrbaum.initiativetracker.dtos.CombatantDTO
import kotlinx.serialization.Serializable

@Serializable
sealed interface ClientCommand {

	@Serializable
	data class AddCombatant(val combatant: CombatantDTO) : ClientCommand
}