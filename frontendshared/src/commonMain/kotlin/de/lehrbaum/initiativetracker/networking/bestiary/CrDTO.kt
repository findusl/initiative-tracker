package de.lehrbaum.initiativetracker.networking.bestiary

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.JsonTransformingSerializer

@Serializable
data class CrDTO(
	val cr: String,
	val lair: String? = null,
	val coven: String? = null,
)

internal object CrDTODeserializer : JsonTransformingSerializer<CrDTO>(CrDTO.serializer()) {
	override fun transformDeserialize(element: JsonElement): JsonElement {
		if (element is JsonPrimitive) {
			return JsonObject(mapOf("cr" to element))
		}
		return element
	}
}
