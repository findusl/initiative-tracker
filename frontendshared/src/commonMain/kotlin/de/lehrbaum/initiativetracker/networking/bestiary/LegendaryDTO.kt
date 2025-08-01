package de.lehrbaum.initiativetracker.networking.bestiary

import kotlinx.serialization.Serializable

@Serializable
data class LegendaryDTO(
	val name: String? = null,
	val entries: List<@Serializable(with = DescriptionEntrySerializer::class) DescriptionEntryDTO>,
)
