package de.lehrbaum.initiativetracker.dtos.commands

import de.lehrbaum.initiativetracker.dtos.CombatantId
import de.lehrbaum.initiativetracker.dtos.CombatantModel
import de.lehrbaum.initiativetracker.dtos.UserId
import kotlinx.serialization.Serializable

@Serializable
sealed interface ClientCommand {

	@Serializable
	data class AddCombatant(val combatant: CombatantModel) : ClientCommand

	@Serializable
	data class EditCombatant(val combatant: CombatantModel) : ClientCommand

	@Serializable
	data class DamageCombatant(val targetId: CombatantId, val damage: Int, val ownerId: UserId) : ClientCommand

	@Serializable
	data class FinishTurn(val activeCombatantIndex: Int) : ClientCommand

	@Serializable
	/**
	 * Attempts to cancel a command. Cancellation is not guaranteed.
	 */
	data object CancelCommand : ClientCommand
}
