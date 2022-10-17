package de.lehrbaum.initiativetracker.networking.bestiary

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.JsonTransformingSerializer

@Serializable
data class TagDTO(
	val tag: String,
	val prefix: String? = null
)

internal object TagDTODeserializer : JsonTransformingSerializer<TagDTO>(TagDTO.serializer()) {
	override fun transformDeserialize(element: JsonElement): JsonElement {
		if (element is JsonPrimitive) {
			return JsonObject(mapOf("tag" to element))
		}
		return element
	}
}
