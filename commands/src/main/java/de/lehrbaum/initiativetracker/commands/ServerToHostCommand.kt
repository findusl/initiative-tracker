package de.lehrbaum.initiativetracker.commands

import kotlinx.serialization.Serializable

@Serializable
sealed interface ServerToHostCommand {
	@Serializable
	data class SessionStarted(val sessionId: Int) : ServerToHostCommand
}