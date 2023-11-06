package de.lehrbaum.initiativetracker.networking.bestiary

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.JsonTransformingSerializer

@Suppress("unused") // No clue why it does not get suppressed by the SerialName annotation
@Serializable
enum class AlignmentDTO {
	@SerialName("A")
	ANY,

	@SerialName("C")
	CHAOTIC,

	@SerialName("E")
	EVIL,

	@SerialName("G")
	GOOD,

	@SerialName("L")
	LAWFUL,

	@SerialName("N")
	NEUTRAL,

	@SerialName("NX")
	X_AXIS_CHOICES,

	@SerialName("NY")
	Y_AXIS_CHOICES,

	@SerialName("U")
	UNALIGNED,
}

internal object AlignmentDTODeserializer : JsonTransformingSerializer<AlignmentDTO>(AlignmentDTO.serializer()) {
	override fun transformDeserialize(element: JsonElement): JsonElement {
		if (element !is JsonPrimitive) {
			// TASK handle chance alignment:
			/*
			"alignment": [
				{
					"alignment": [
						"N",
						"G"
					],
					"chance": 50
				},
				{
					"alignment": [
						"N",
						"E"
					],
					"chance": 50
				}
			],
			 */
			return JsonPrimitive("U")
		}
		return element
	}
}