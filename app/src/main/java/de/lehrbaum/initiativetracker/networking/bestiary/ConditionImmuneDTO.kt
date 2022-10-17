package de.lehrbaum.initiativetracker.networking.bestiary

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.JsonTransformingSerializer

@Serializable
data class ConditionImmuneDTO(
	val condition: String? = null,
	@SerialName("conditionImmune")
	val conditionsImmune: List<@Serializable(with = ConditionImmuneDTODeserializer::class) ConditionImmuneDTO>? = null,
	val preNote: String? = null
)

internal object ConditionImmuneDTODeserializer : JsonTransformingSerializer<ConditionImmuneDTO>(ConditionImmuneDTO.serializer()) {
	override fun transformDeserialize(element: JsonElement): JsonElement {
		if (element is JsonPrimitive) {
			return JsonObject(mapOf("condition" to element))
		}
		return element
	}
}
