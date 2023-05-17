package de.lehrbaum.initiativetracker.commands

import de.lehrbaum.initiativetracker.dtos.CombatDTO
import kotlinx.serialization.Serializable

@Serializable
sealed interface StartCommand {

	@Serializable
	sealed interface HostingCommand : StartCommand

	@Serializable
	class JoinAsHost(
		val sessionId: Int
	) : HostingCommand {
		@Serializable
		sealed interface Response

		@Serializable
		data class JoinedAsHost(val combatDTO: CombatDTO) : Response

		@Serializable
		object SessionAlreadyHasHost : Response

		@Serializable
		object SessionNotFound : Response
	}

	@Serializable
	data class JoinSession(
		val sessionId: Int
	) : StartCommand
}
