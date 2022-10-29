package de.lehrbaum.initiativetracker.commands

import de.lehrbaum.initiativetracker.dtos.CombatDTO
import kotlinx.serialization.Serializable

@Serializable
sealed interface ServerToClientCommand {
	@Serializable
	data class CombatUpdatedCommand(val combat: CombatDTO) : ServerToClientCommand
}

