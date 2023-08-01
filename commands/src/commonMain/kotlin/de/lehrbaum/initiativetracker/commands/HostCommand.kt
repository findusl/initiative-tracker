package de.lehrbaum.initiativetracker.commands

import de.lehrbaum.initiativetracker.dtos.CombatModel
import kotlinx.serialization.Serializable

@Serializable
sealed interface HostCommand {

	@Serializable
	data class CombatUpdatedCommand(val combat: CombatModel) : HostCommand

	@Serializable
	data class CommandCompleted(val accepted: Boolean): HostCommand

}
