package de.lehrbaum.initiativetracker.data

import kotlinx.serialization.Serializable

@Serializable
data class CombatLink(
	val backendUri: BackendUri,
	val isHost: Boolean,
	val sessionId: SessionId? = null,
) {
	val userDescription = (sessionId?.let { "$it " } ?: "") + "on ${backendUri.hostName}"
}

@Serializable
data class SessionId(val id: Int)

// TASK when host contains a path
@Serializable
data class BackendUri(
	val secureConnection: Boolean,
	val hostName: String,
	val port: Int,
)
