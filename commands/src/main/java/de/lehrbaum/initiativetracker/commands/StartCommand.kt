package de.lehrbaum.initiativetracker.commands

import kotlinx.serialization.Serializable

@Serializable
sealed interface StartCommand {

	@Serializable
	class StartHosting() : StartCommand

	@Serializable
	data class JoinSession(
		val sessionId: Int
	) : StartCommand
}
