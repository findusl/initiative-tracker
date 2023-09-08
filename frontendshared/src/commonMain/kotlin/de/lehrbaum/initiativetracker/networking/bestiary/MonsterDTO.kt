package de.lehrbaum.initiativetracker.networking.bestiary

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonPrimitive

@Serializable
data class MonsterDTO(
	val ac: List<@Serializable(with = AcDTODeserializer::class) AcDTO>? = null,
	val action: List<ActionDTO>? = null,
	val actionTags: List<String>? = null,
	val alias: List<String>? = null,
	val alignment: List<@Serializable(with = AlignmentDTODeserializer::class) AlignmentDTO>? = null,
	val altArt: List<AltArtDTO>? = null,
	val basicRules: Boolean = false,
	val cha: Int? = null,
	val con: Int? = null,
	val conditionImmune: List<@Serializable(with = ConditionImmuneDTODeserializer::class) ConditionImmuneDTO>? = null,
	val conditionInflict: List<String>? = null,
	val conditionInflictLegendary: List<String>? = null,
	val conditionInflictSpell: List<String>? = null,
	// @SerialName("_copy")
	// val copy: Any? = null // describes how to generate this unit by copying it from another
	val cr: @Serializable(with = CrDTODeserializer::class) CrDTO? = null,
	val damageTags: List<String>? = null,
	val damageTagsLegendary: List<String>? = null,
	val damageTagsSpell: List<String>? = null,
	val dex: Int? = null,
	val dragonAge: String? = null,
	val dragonCastingColor: String? = null,
	val environment: List<String>? = null,
	val familiar: Boolean = false,
	val group: List<String>? = null,
	val hasFluff: Boolean = false,
	val hasFluffImages: Boolean = false,
	val hasToken: Boolean = false,
	val hp: HpDTO? = null,
	val immune: List<@Serializable(with = DamageTypeDTODeserializer::class) DamageTypeDTO>? = null,
	val int: Int? = null,
	val languageTags: List<String>? = null,
	val languages: List<String>? = null,
	val legendary: List<LegendaryDTO>? = null,
	val legendaryGroup: LegendaryGroupDTO? = null,
	val legendaryHeader: List<String>? = null,
	val miscTags: List<String>? = null,
	val name: String,
	val otherSources: List<OtherSourceDTO>? = null,
	val page: Int? = null,
	val passive: JsonPrimitive? = null,
	val reaction: List<ReactionDTO>? = null,
	val resist: List<@Serializable(with = DamageTypeDTODeserializer::class) DamageTypeDTO>? = null,
	val save: SaveDTO? = null,
	val senseTags: List<String>? = null,
	val senses: List<String>? = null,
	val size: List<String>? = null,
	val skill: SkillDTO? = null,
	// val soundClip: SoundClipDTO? = null, don't need sound
	val source: String,
	val speed: SpeedsDTO? = null,
	val spellcasting: List<SpellcastingDTO>? = null,
	val spellcastingTags: List<String>? = null,
	val srd: Boolean = false,
	val str: Int? = null,
	val trait: List<TraitDTO>? = null,
	val traitTags: List<String>? = null,
	@Serializable(with = TypeDTODeserializer::class)
	val type: TypeDTO? = null,
	val variant: List<VariantDTO>? = null,
	@SerialName("_versions")
	val versions: List<VersionDTO>? = null,
	val vulnerable: List<@Serializable(with = DamageTypeDTODeserializer::class) DamageTypeDTO>? = null,
	val wis: Int? = null
) {
	@kotlinx.serialization.Transient
	val displayName = "$name ($source)"
}