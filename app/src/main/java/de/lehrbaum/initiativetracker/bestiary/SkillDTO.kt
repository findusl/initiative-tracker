package de.lehrbaum.initiativetracker.bestiary

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SkillDTO(
	val acrobatics: String? = null,
	val arcana: String? = null,
	val athletics: String? = null,
	val deception: String? = null,
	val history: String? = null,
	val insight: String? = null,
	val intimidation: String? = null,
	val investigation: String? = null,
	val medicine: String? = null,
	val nature: String? = null,
	val perception: String? = null,
	val performance: String? = null,
	val persuasion: String? = null,
	val religion: String? = null,
	@SerialName("sleight of hand")
	val sleightOfHand: String? = null,
	val stealth: String? = null,
	val survival: String? = null
)