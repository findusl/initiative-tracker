package de.lehrbaum.initiativetracker.networking.bestiary

import kotlinx.serialization.Serializable

@Serializable
data class SaveDTO(
	val cha: String? = null,
	val con: String? = null,
	val dex: String? = null,
	val int: String? = null,
	val str: String? = null,
	val wis: String? = null,
)
