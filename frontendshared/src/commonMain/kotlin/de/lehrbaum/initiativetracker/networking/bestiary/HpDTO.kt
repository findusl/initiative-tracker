package de.lehrbaum.initiativetracker.networking.bestiary

import kotlinx.serialization.Serializable

@Serializable
data class HpDTO(
	val average: Int? = null,
	val formula: String? = null,
	val special: String? = null,
) {
	init {
		require(average != null || special != null) { "Either average or special is required" }
	}
}
