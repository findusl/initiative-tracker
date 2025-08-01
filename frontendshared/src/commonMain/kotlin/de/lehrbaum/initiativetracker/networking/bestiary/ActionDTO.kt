package de.lehrbaum.initiativetracker.networking.bestiary

import kotlinx.serialization.Serializable

@Serializable
data class ActionDTO(
	val entries: List<@Serializable(with = DescriptionEntrySerializer::class) DescriptionEntryDTO>,
	val name: String,
)
