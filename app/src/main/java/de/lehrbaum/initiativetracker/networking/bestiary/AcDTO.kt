package de.lehrbaum.initiativetracker.networking.bestiary

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.JsonTransformingSerializer

@Serializable
data class AcDTO(
	val ac: Int? = null,
	val from: List<String>? = null,
	val condition: String? = null,
	val braces: Boolean = false,
	val special: String? = null
)

internal object AcDTODeserializer : JsonTransformingSerializer<AcDTO>(
	AcDTO.serializer()
) {
	override fun transformDeserialize(element: JsonElement): JsonElement {
		if (element is JsonPrimitive) {
			return JsonObject(mapOf("ac" to element))
		}
		return element
	}
}