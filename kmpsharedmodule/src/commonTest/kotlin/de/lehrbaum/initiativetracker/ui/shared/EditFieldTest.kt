package de.lehrbaum.initiativetracker.ui.shared

import kotlin.test.Test
import kotlin.test.assertNotEquals

internal class EditFieldTest {
	@Test
	fun `Ensure equals works as expected`() {
		val first = EditField(initialValue = "", parseInput = { Result.success(it) })
		val second = EditField(initialValue = "", parseInput = { Result.success(it) })

		assertNotEquals(first, second)
	}
}