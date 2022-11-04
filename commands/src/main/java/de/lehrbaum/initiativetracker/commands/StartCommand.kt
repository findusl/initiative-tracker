package de.lehrbaum.initiativetracker.commands

import de.lehrbaum.initiativetracker.dtos.CombatDTO
import kotlinx.serialization.Serializable

@Serializable
sealed interface StartCommand {

	@Serializable
	class StartHosting(
		val combatDTO: CombatDTO
	) : StartCommand

	@Serializable
	data class JoinSession(
		val sessionId: Int
	) : StartCommand
}
