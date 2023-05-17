package de.lehrbaum.initiativetracker.commands

import de.lehrbaum.initiativetracker.dtos.CombatDTO
import kotlinx.serialization.Serializable

@Serializable
sealed interface HostCommand {

	@Serializable
	data class CombatUpdatedCommand(val combat: CombatDTO) : HostCommand

	@Serializable
	data class CommandCompleted(val accepted: Boolean): HostCommand

}
