package de.lehrbaum.initiativetracker.bestiary

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DailyDTO(
	@SerialName("1e")
	val onceEach: List<@Serializable(with = SpellDTODeserializer::class) SpellDTO> = listOf(),
	@SerialName("2e")
	val twiceEach: List<@Serializable(with = SpellDTODeserializer::class) SpellDTO> = listOf(),
	@SerialName("3e")
	val thriceEach: List<@Serializable(with = SpellDTODeserializer::class) SpellDTO> = listOf(),
	// Unclear whats the difference between onceEach and once. See https://5e.tools/bestiary.html#cambion_mm
	@SerialName("1")
	val once: List<@Serializable(with = SpellDTODeserializer::class) SpellDTO> = listOf(),
	@SerialName("2")
	val twice: List<@Serializable(with = SpellDTODeserializer::class) SpellDTO> = listOf(),
	@SerialName("3")
	val thrice: List<@Serializable(with = SpellDTODeserializer::class) SpellDTO> = listOf()
)