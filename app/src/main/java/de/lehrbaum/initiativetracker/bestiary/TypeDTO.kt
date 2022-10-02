package de.lehrbaum.initiativetracker.bestiary

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.JsonTransformingSerializer

@Serializable
data class TypeDTO(
	val tags: List<@Serializable(with = TagDTODeserializer::class) TagDTO> = listOf(),
	val type: String
)

internal object TypeDTODeserializer : JsonTransformingSerializer<TypeDTO>(TypeDTO.serializer()) {
	override fun transformDeserialize(element: JsonElement): JsonElement {
		if (element is JsonPrimitive) {
			return JsonObject(mapOf("type" to element))
		}
		return element
	}
}
