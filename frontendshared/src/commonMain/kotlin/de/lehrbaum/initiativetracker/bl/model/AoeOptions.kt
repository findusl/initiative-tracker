package de.lehrbaum.initiativetracker.bl.model

// In the future needs conditions as option, damage type and more
data class AoeOptions(
	val damage: Int,
	val save: SaveDC?,
	val halfOnFailure: Boolean,
)

data class SaveDC(
	val type: SavingThrow,
	val difficultyClass: Int,
)
