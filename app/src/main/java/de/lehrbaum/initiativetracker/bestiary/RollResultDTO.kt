package de.lehrbaum.initiativetracker.bestiary

import kotlinx.serialization.Serializable

@Serializable
data class RollResultDTO(
	val exact: Int = -1,
	val max: Int = -1,
	val min: Int = -1,
	val pad: Boolean = false,
) {
	init {
		require(exact != -1 || (max != -1 && min != -1)) {
			"Either exact value or min and max for roll have to be present."
		}
	}
}
