package de.lehrbaum.initiativetracker.networking.bestiary

import kotlinx.serialization.Serializable

@Serializable
data class VersionDTO(
	val ac: List<AcDTO>? = null,
	val action: List<ActionDTO>? = null,
	val alignment: List<String>? = null,
	val cr: @Serializable(with = CrDTODeserializer::class) CrDTO? = null,
	// val hp: HpDTO? = null, weird field, only troll has it and unclear how used https://5e.tools/bestiary.html#troll_mm
	val int: Int? = null,
	val languages: List<String>? = null,
	// @SerialName("_mod")
	// val mod: ModDTO? = null, very complicated on how to modify the creature
	val name: String,
	val senses: List<String>? = null,
	val size: String? = null,
	val skill: List<String>? = null,
	val source: String,
	val speed: SpeedsDTO? = null,
	val spellcasting: List<SpellcastingDTO>? = null,
	val variant: VariantDTO? = null,
	val wis: Int? = null
)