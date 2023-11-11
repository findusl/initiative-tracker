package de.lehrbaum.initiativetracker.networking.bestiary

import kotlinx.serialization.Serializable

@Serializable
data class CopyDTO(
	val name: String,
	val source: String,
) {
	@kotlinx.serialization.Transient
	val displayName = "$name ($source)"
}
