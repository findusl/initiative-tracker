package de.lehrbaum.initiativetracker.commands

import de.lehrbaum.initiativetracker.dtos.CombatantDTO
import kotlinx.serialization.Serializable

@Serializable
sealed interface ClientCommand {

	@Serializable
	data class AddCombatant(val combatant: CombatantDTO) : ClientCommand

	@Serializable
	data class EditCombatant(val combatant: CombatantDTO) : ClientCommand

	@Serializable
	/**
	 * Attempts to cancel a command. Cancellation is not guaranteed.
	 */
	object CancelCommand : ClientCommand
}
