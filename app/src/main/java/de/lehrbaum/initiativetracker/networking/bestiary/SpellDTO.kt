package de.lehrbaum.initiativetracker.networking.bestiary

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.JsonTransformingSerializer

@Serializable
data class SpellDTO(
	@SerialName("entry")
	val spellReference: String,
	val hidden: Boolean = false
)

internal object SpellDTODeserializer : JsonTransformingSerializer<SpellDTO>(SpellDTO.serializer()) {
	override fun transformDeserialize(element: JsonElement): JsonElement {
		if (element is JsonPrimitive) {
			return JsonObject(mapOf("entry" to element))
		}
		return element
	}
}
