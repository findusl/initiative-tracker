package de.lehrbaum.initiativetracker.bl

import kotlin.random.Random

object Dice {
	fun d20() = Random.nextInt(20) + 1

	fun calculateDiceFormula(formula: String, seed: Long): CalculationResult? {
		val cleanedFormula = formula.trim().replace('#', 'd').replace("\\s".toRegex(), "")
		if (cleanedFormula.toIntOrNull() != null) return null // A single number is a valid formula but not what we need here
		val elements = cleanedFormula.split("+", "-")
		val operators = cleanedFormula.toCharArray().filter { it == '+' || it == '-' }
		if (operators.size != elements.size - 1) return null

		var intermediateStep = ""
		var result = 0
		val random = Random(seed)

		for (i in elements.indices) {
			val operator = if (i == 0) null else operators[i - 1]
			val sign = if (operator == '-') -1 else +1
			val element = elements[i]
			var elementSum = element.toIntOrNull()
			if (elementSum == null) {
				val parts = if (element.contains('d')) element.split('d') else return null
				val rolls = parts[0].toIntOrNull() ?: return null
				val sides = parts[1].toIntOrNull() ?: return null

				elementSum = (0 until rolls).sumOf { random.nextInt(sides) + 1 }
			}
			result += elementSum * sign
			intermediateStep += (operator?.toString() ?: "") + elementSum
		}

		return CalculationResult(intermediateStep = intermediateStep, sum = result)
	}

	data class CalculationResult(val intermediateStep: String, val sum: Int)
}
