package de.lehrbaum.initiativetracker.commands

import de.lehrbaum.initiativetracker.dtos.CombatantModel
import kotlinx.serialization.Serializable

@Serializable
sealed interface ClientCommand {

	@Serializable
	data class AddCombatant(val combatant: CombatantModel) : ClientCommand

	@Serializable
	data class EditCombatant(val combatant: CombatantModel) : ClientCommand

	@Serializable
	data class DamageCombatant(val combatantId: Long, val damage: Int, val ownerId: Long) : ClientCommand

	@Serializable
	/**
	 * Attempts to cancel a command. Cancellation is not guaranteed.
	 */
	object CancelCommand : ClientCommand
}
