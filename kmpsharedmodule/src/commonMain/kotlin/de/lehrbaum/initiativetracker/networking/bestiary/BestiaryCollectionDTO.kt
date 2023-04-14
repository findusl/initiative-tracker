package de.lehrbaum.initiativetracker.networking.bestiary

import kotlinx.serialization.Serializable


@Serializable
data class BestiaryCollectionDTO(
	val monster: List<MonsterDTO>
)

