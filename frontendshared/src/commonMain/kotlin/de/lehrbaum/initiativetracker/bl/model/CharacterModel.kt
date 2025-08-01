package de.lehrbaum.initiativetracker.bl.model

import kotlinx.serialization.Serializable

@Serializable
data class CharacterModel(
	val id: Long,
	val name: String,
	val initiativeMod: Int?,
	val maxHp: Int?,
)
