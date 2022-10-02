package de.lehrbaum.initiativetracker.bestiary

import kotlinx.serialization.Serializable

@Serializable
data class AltArtDTO(
	val name: String,
	val page: Int? = null,
	val source: String
)