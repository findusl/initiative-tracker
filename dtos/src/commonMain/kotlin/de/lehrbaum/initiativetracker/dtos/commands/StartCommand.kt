package de.lehrbaum.initiativetracker.dtos.commands

import de.lehrbaum.initiativetracker.dtos.CombatModel
import kotlinx.serialization.Serializable

@Serializable
sealed interface StartCommand {
	@Serializable
	sealed interface HostingCommand : StartCommand {
		@Serializable
		sealed interface Response

		@Serializable
		data class JoinedAsHost(val combatModel: CombatModel) : Response

		@Serializable
		data object SessionAlreadyHasHost : Response

		@Serializable
		data object SessionNotFound : Response
	}

	@Serializable
	data object JoinDefaultSessionAsHost : HostingCommand

	@Serializable
	class JoinAsHostById(val sessionId: Int) : HostingCommand

	@Serializable
	sealed interface JoiningCommand : StartCommand

	@Serializable
	data object JoinDefaultSession : JoiningCommand

	@Serializable
	data class JoinSessionById(val sessionId: Int) : JoiningCommand
}
