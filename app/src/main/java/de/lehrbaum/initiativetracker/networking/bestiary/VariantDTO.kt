package de.lehrbaum.initiativetracker.networking.bestiary

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VariantDTO(
	val entries: List<@Serializable(with = DescriptionEntrySerializer::class) DescriptionEntryDTO>,
	val name: String,
	val page: Int? = null,
	val source: String? = null,
	val token: TokenDTO? = null,
	val type: String,
	@SerialName("_version")
	val version: VariantVersionDTO? = null
)