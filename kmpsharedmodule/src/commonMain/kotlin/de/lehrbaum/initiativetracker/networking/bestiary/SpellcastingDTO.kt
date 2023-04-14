package de.lehrbaum.initiativetracker.networking.bestiary

import kotlinx.serialization.Serializable

@Serializable
data class SpellcastingDTO(
	/** can miss if the dc is fixed, like when the casting comes from an item */
	val ability: String? = null,
	val daily: DailyDTO? = null,
	val footerEntries: List<String>? = null,
	val headerEntries: List<String>? = null,
	val hidden: List<String>? = null,
	val name: String,
	val spells: SpellsDTO? = null,
	val displayAs: String? = null,
	val type: String? = null,
	val will: List<@Serializable(with = SpellDTODeserializer::class) SpellDTO> = listOf()
)