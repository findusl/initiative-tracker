package de.lehrbaum.initiativetracker.networking.bestiary

import kotlinx.serialization.Serializable

@Serializable
data class VariantVersionDTO(
	val addAs: String? = null,
	val addHeadersAs: String? = null,
	val name: String? = null,
)
