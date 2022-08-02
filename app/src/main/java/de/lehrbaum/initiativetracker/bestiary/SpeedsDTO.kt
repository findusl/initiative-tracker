package de.lehrbaum.initiativetracker.bestiary

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.JsonTransformingSerializer

@Serializable
data class SpeedsDTO(
	val burrow: @Serializable(with = SpeedDTODeserializer::class) SpeedDTO? = null,
	val canHover: Boolean = false,
	val climb: @Serializable(with = SpeedDTODeserializer::class) SpeedDTO? = null,
	val fly: @Serializable(with = SpeedDTODeserializer::class) SpeedDTO? = null,
	val swim: @Serializable(with = SpeedDTODeserializer::class) SpeedDTO? = null,
	val walk: @Serializable(with = SpeedDTODeserializer::class) SpeedDTO? = null
)

@Serializable
data class SpeedDTO(
	val number: Int,
	val condition: String? = null,
)

internal object SpeedDTODeserializer : JsonTransformingSerializer<SpeedDTO>(SpeedDTO.serializer()) {
	override fun transformDeserialize(element: JsonElement): JsonElement {
		if (element is JsonPrimitive) {
			return JsonObject(mapOf("number" to element))
		}
		return element
	}
}
