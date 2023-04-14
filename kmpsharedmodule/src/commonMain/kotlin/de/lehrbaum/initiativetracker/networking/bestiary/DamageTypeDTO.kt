package de.lehrbaum.initiativetracker.networking.bestiary

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.JsonTransformingSerializer

@Serializable
data class DamageTypeDTO(
	val damageType: String? = null,
	@SerialName("immune")
	val damageTypes: List<@Serializable(with = DamageTypeDTODeserializer::class) DamageTypeDTO>? = null,
	val cond: Boolean = false,
	val note: String? = null,
)

internal object DamageTypeDTODeserializer : JsonTransformingSerializer<DamageTypeDTO>(DamageTypeDTO.serializer()) {
	override fun transformDeserialize(element: JsonElement): JsonElement {
		if (element is JsonPrimitive) {
			return JsonObject(mapOf("damageType" to element))
		}
		return element
	}
}
