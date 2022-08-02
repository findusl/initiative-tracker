package de.lehrbaum.initiativetracker.bestiary

import kotlinx.serialization.Serializable

@Serializable
data class TraitDTO(
	val entries: List<@Serializable(with = DescriptionEntrySerializer::class) DescriptionEntryDTO>,
	val name: String
)