package de.lehrbaum.initiativetracker.dtos.commands

import de.lehrbaum.initiativetracker.dtos.CombatModel
import kotlinx.serialization.Serializable

@Serializable
sealed interface JoinSessionResponse {
	@Serializable
	data class JoinedSession(val combatModel: CombatModel) : JoinSessionResponse

	@Serializable
	object SessionNotFound : JoinSessionResponse
}
