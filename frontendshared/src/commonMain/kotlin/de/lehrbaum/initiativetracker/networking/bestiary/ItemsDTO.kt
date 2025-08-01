package de.lehrbaum.initiativetracker.networking.bestiary

import kotlinx.serialization.Serializable

@Serializable
data class ItemsDTO(
	val ability: String,
	val footerEntries: List<String>,
	val headerEntries: List<String>,
	val name: String,
	val spells: SpellsDTO,
)
