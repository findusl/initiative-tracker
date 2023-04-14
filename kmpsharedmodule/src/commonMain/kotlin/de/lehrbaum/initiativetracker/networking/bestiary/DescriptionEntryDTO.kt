package de.lehrbaum.initiativetracker.networking.bestiary

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.JsonTransformingSerializer

@Serializable
sealed class DescriptionEntryDTO

@Serializable
@SerialName("simple")
data class SimpleEntryDTO(
	val content: String
) : DescriptionEntryDTO()

@Serializable
@SerialName("entries")
data class SubEntries(
	val name: String? = null,
	val entries: List<@Serializable(with = DescriptionEntrySerializer::class) DescriptionEntryDTO>,
) : DescriptionEntryDTO()

@Serializable
@SerialName("quote")
data class QuoteEntries(
	val by: String,
	val entries: List<@Serializable(with = DescriptionEntrySerializer::class) DescriptionEntryDTO>,
) : DescriptionEntryDTO()

@Serializable
@SerialName("inset")
data class InsetEntries(
	val source: String,
	val page: Int,
	val name: String,
	val entries: List<@Serializable(with = DescriptionEntrySerializer::class) DescriptionEntryDTO>,
) : DescriptionEntryDTO()

@Serializable
@SerialName("item")
data class ItemEntries(
	val name: String,
	/** Sometimes there is just a single entry. We parse it so that entries always contains the single entry as well. */
	val entry: @Serializable(with = DescriptionEntrySerializer::class) DescriptionEntryDTO? = null,
	val entries: List<@Serializable(with = DescriptionEntrySerializer::class) DescriptionEntryDTO> = listOfNotNull(entry),
) : DescriptionEntryDTO()

@Serializable
@SerialName("list")
data class ListEntryDTO(
	val style: String? = null,
	val items: List<@Serializable(with = DescriptionEntrySerializer::class) DescriptionEntryDTO>,
) : DescriptionEntryDTO()

@Serializable
@SerialName("table")
data class TableEntryDTO(
	val caption: String? = null,
	val colLabels: List<String>,
	val colStyles: List<String>,
	val rows: List<List<@Serializable(with = TableCellSerializer::class) TableCellDTO>>
) : DescriptionEntryDTO()

@Serializable
@SerialName("spellcasting")
data class SpellcastingEntryDTO(
	val name: String,
	val headerEntries: List<@Serializable(with = DescriptionEntrySerializer::class) DescriptionEntryDTO>,
	val footerEntries: List<@Serializable(with = DescriptionEntrySerializer::class) DescriptionEntryDTO> = listOf(),
	val spells: SpellsDTO? = null,
	val will: List<String> = listOf(),
	val daily: DailyDTO? = null,
	val ability: String? = null
) : DescriptionEntryDTO()

@Serializable
@SerialName("variantSub")
data class VariantSubEntryDTO(
	val name: String,
	val entries: List<@Serializable(with = DescriptionEntrySerializer::class) DescriptionEntryDTO>,
) : DescriptionEntryDTO()

@Serializable
@SerialName("variantInner")
data class VariantInnerEntryDTO(
	val name: String,
	val entries: List<@Serializable(with = DescriptionEntrySerializer::class) DescriptionEntryDTO>,
) : DescriptionEntryDTO()

internal object DescriptionEntrySerializer : JsonTransformingSerializer<DescriptionEntryDTO>(DescriptionEntryDTO.serializer()) {
	override fun transformDeserialize(element: JsonElement): JsonElement {
		if (element is JsonPrimitive) {
			return JsonObject(
				mapOf(
					"content" to element,
					"type" to JsonPrimitive("simple")
				)
			)
		}
		require(element is JsonObject && element["type"] != null) { "Description entry is invalid $element" }
		return element
	}
}
