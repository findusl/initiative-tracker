package de.lehrbaum.initiativetracker.dtos.commands

import de.lehrbaum.initiativetracker.dtos.CombatModel
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
		data class JoinedAsHost(val combatModel: CombatModel) : Response

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
