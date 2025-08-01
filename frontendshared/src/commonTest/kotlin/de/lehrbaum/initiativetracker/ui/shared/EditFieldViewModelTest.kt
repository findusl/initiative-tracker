package de.lehrbaum.initiativetracker.ui.shared

import kotlin.test.Test
import kotlin.test.assertNotEquals

internal class EditFieldViewModelTest {
	/**
	 * This test ensures there is no equals in that ViewModel, because otherwise the compose optimizations can make trouble
	 */
	@Test
	fun `Ensure equals works as expected`() {
		val first = EditFieldViewModel(initialValue = "", parseInput = { Result.success(it) })
		val second = EditFieldViewModel(initialValue = "", parseInput = { Result.success(it) })

		assertNotEquals(first, second)
	}
}
