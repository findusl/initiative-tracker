package de.lehrbaum.initiativetracker.networking.bestiary

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class YearlyDTO(
	@SerialName("1e")
	val e: List<String>
)