package de.lehrbaum.initiativetracker.bestiary

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.JsonTransformingSerializer

@Serializable
sealed class TableCellDTO

@Serializable
@SerialName("simple")
data class SimpleCellDTO(
	val content: String
) : TableCellDTO()

@Serializable
@SerialName("cell")
data class RollCellDTO(
	val roll: RollResultDTO
) : TableCellDTO()

@Serializable
@SerialName("entries")
data class EntriesCellDTO(
	val name: String? = null,
	val entries: List<@Serializable(with = DescriptionEntrySerializer::class) DescriptionEntryDTO>,
) : TableCellDTO()

internal object TableCellSerializer : JsonTransformingSerializer<TableCellDTO>(TableCellDTO.serializer()) {
	override fun transformDeserialize(element: JsonElement): JsonElement {
		if (element is JsonPrimitive) {
			return JsonObject(
				mapOf(
					"content" to element,
					"type" to JsonPrimitive("simple")
				)
			)
		}
		return element
	}
}
