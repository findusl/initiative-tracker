package de.lehrbaum.initiativetracker.bestiary

import kotlinx.serialization.Serializable


@Serializable
data class BestiaryCollectionDTO(
	val monster: List<MonsterDTO>
)

