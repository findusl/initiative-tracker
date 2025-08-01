package de.lehrbaum.initiativetracker.dtos.commands

import de.lehrbaum.initiativetracker.dtos.CombatModel
import kotlinx.serialization.Serializable

@Serializable
sealed interface ServerToClientCommand {
	@Serializable
	data class CombatUpdatedCommand(val combat: CombatModel) : ServerToClientCommand

	@Serializable
	object CombatEnded : ServerToClientCommand

	@Serializable
	data class CommandCompleted(val accepted: Boolean) : ServerToClientCommand
}
