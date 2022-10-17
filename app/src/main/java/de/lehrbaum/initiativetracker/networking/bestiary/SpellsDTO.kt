package de.lehrbaum.initiativetracker.networking.bestiary

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SpellsDTO(
	@SerialName("0")
	val cantrips: SpellsForLevelDTO? = null,
	@SerialName("1")
	val firstLevel: SpellsForLevelDTO? = null,
	@SerialName("2")
	val secondLevel: SpellsForLevelDTO? = null,
	@SerialName("3")
	val thirdLevel: SpellsForLevelDTO? = null,
	@SerialName("4")
	val fourthLevel: SpellsForLevelDTO? = null,
	@SerialName("5")
	val fifthLevel: SpellsForLevelDTO? = null,
	@SerialName("6")
	val sixthLevel: SpellsForLevelDTO? = null,
	@SerialName("7")
	val seventhLevel: SpellsForLevelDTO? = null,
	@SerialName("8")
	val eightLevel: SpellsForLevelDTO? = null,
	@SerialName("9")
	val ninthLevel: SpellsForLevelDTO? = null
)