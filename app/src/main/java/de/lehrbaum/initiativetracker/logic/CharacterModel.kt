package de.lehrbaum.initiativetracker.logic

import kotlinx.serialization.Serializable

@Serializable
data class CharacterModel(
	val id: Long,
	val name: String,
	val initiativeBonus: Short,
)
