package de.lehrbaum.initiativetracker.networking.bestiary

import kotlinx.serialization.Serializable

@Serializable
data class AltArtDTO(
	val name: String,
	val page: Int? = null,
	val source: String
)