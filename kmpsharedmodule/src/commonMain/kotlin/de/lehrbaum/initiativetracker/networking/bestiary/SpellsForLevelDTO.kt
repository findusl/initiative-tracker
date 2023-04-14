package de.lehrbaum.initiativetracker.networking.bestiary

import kotlinx.serialization.Serializable

@Serializable
data class SpellsForLevelDTO(
	/** null means as often as wanted */
	val slots: Int? = null,
	val spells: List<@Serializable(with = SpellDTODeserializer::class) SpellDTO>
)