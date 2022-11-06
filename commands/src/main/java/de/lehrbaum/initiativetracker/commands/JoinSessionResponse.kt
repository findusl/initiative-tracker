package de.lehrbaum.initiativetracker.commands

import de.lehrbaum.initiativetracker.dtos.CombatDTO
import kotlinx.serialization.Serializable

@Serializable
sealed interface JoinSessionResponse {

	@Serializable
	data class JoinedSession(val combatDTO: CombatDTO) : JoinSessionResponse

	@Serializable
	object SessionNotFound : JoinSessionResponse
}
