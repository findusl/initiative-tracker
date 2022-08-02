package de.lehrbaum.initiativetracker.bestiary

import kotlinx.serialization.Serializable

@Serializable
data class LegendaryGroupDTO(
	val name: String,
	val source: String
)