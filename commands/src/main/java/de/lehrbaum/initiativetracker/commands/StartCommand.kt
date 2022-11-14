package de.lehrbaum.initiativetracker.commands

import de.lehrbaum.initiativetracker.dtos.CombatDTO
import kotlinx.serialization.Serializable

@Serializable
sealed interface StartCommand {

	@Serializable
	class StartHosting(
		val combatDTO: CombatDTO
	) : StartCommand {
		@Serializable
		sealed interface Response

		@Serializable
		data class SessionStarted(val sessionId: Int) : Response
	}

	@Serializable
	class JoinAsHost(
		val sessionId: Int
	) : StartCommand {
		@Serializable
		sealed interface Response

		@Serializable
		data class JoinedAsHost(val combatDTO: CombatDTO) : Response

		@Serializable
		object SessionAlreadyHasHost : Response
	}

	@Serializable
	data class JoinSession(
		val sessionId: Int
	) : StartCommand
}
