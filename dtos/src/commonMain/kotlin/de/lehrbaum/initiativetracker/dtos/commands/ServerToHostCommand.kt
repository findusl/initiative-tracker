package de.lehrbaum.initiativetracker.dtos.commands

import de.lehrbaum.initiativetracker.dtos.CombatantId
import de.lehrbaum.initiativetracker.dtos.CombatantModel
import de.lehrbaum.initiativetracker.dtos.UserId
import kotlinx.serialization.Serializable

@Serializable
sealed interface ServerToHostCommand {

	@Serializable
	data class AddCombatant(val combatant: CombatantModel) : ServerToHostCommand

	@Serializable
	data class EditCombatant(val combatant: CombatantModel) : ServerToHostCommand

	@Serializable
	data class DamageCombatant(val targetId: CombatantId, val damage: Int, val ownerId: UserId) : ServerToHostCommand
}