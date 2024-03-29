package de.lehrbaum.initiativetracker.networking.bestiary

import kotlinx.serialization.Serializable

@Serializable
data class TokenDTO(
	val name: String,
	val page: Int,
	val source: String
)