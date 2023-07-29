package de.lehrbaum.initiativetracker.bl

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DiceTest {

	private val seed = 1234L

    @Test
    fun calculateDiceFormulaInvalidFormula() {
		assertNull(Dice.calculateDiceFormula("8d6-2d", seed))
    }

    @Test
    fun calculateDiceFormulaValidFormula() {
		val result = Dice.calculateDiceFormula("8d6-2d4+2", seed)!!
		assertEquals(25, result.sum)
		assertEquals("31-8+2", result.intermediateStep)
    }
}