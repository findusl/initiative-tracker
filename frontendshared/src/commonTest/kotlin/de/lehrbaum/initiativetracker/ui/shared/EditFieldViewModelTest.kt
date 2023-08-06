package de.lehrbaum.initiativetracker.ui.shared

import kotlin.test.Test
import kotlin.test.assertNotEquals

internal class EditFieldViewModelTest {
	@Test
	fun `Ensure equals works as expected`() {
		val first = EditFieldViewModel(initialValue = "", parseInput = { Result.success(it) })
		val second = EditFieldViewModel(initialValue = "", parseInput = { Result.success(it) })

		assertNotEquals(first, second)
	}
}