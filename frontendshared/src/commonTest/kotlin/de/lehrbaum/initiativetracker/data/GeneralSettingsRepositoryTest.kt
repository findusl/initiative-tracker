package de.lehrbaum.initiativetracker.data

import com.russhwolf.settings.MapSettings
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.test.runTest

class GeneralSettingsRepositoryTest {
	val repository = GeneralSettingsRepository(settings = MapSettings.Factory().create("test_settings"))

	@Test
	fun testGuideDismissal() =
		runTest(timeout = 1_000.milliseconds) {
			val testGuideKey = "test_guide_key"

			val guideFlow = repository.showGuideFlow(testGuideKey).stateIn(backgroundScope)
			assertTrue(guideFlow.value, "Guide should not be dismissed yet")

			repository.hideGuide(testGuideKey)

			assertFalse(guideFlow.filter { !it }.first(), "Guide should no longer show")
		}

	@Test
	fun testHomebrewLinksListSerialization() {
		// Get the default homebrew links
		val defaultLinks = repository.homebrewLinks

		// Verify default links are not empty (they should contain the Tome of Beasts links)
		assertTrue(defaultLinks.isNotEmpty(), "Default homebrew links should not be empty")
		assertTrue(
			defaultLinks.any { it.contains("Tome%20of%20Beasts") },
			"Default links should contain Tome of Beasts links",
		)

		// Set a custom list of links
		val customLinks = listOf(
			"https://example.com/homebrew1.json",
			"https://example.com/homebrew2.json",
		)
		repository.homebrewLinks = customLinks

		// Verify the links were saved correctly
		assertContentEquals(
			customLinks,
			repository.homebrewLinks,
			"Repository should return the custom links we just set",
		)

		// Test with an empty list
		repository.homebrewLinks = emptyList()
		assertEquals(
			0,
			repository.homebrewLinks.size,
			"Repository should save and return an empty list",
		)
	}
}
