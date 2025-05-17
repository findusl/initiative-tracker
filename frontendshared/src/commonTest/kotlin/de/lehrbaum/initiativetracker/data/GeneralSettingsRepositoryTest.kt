package de.lehrbaum.initiativetracker.data

import com.russhwolf.settings.MapSettings
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds

class GeneralSettingsRepositoryTest {

	val repository = GeneralSettingsRepository(settings = MapSettings.Factory().create("test_settings"))
    
    @Test
    fun testGuideDismissal() = runTest(timeout = 1_000.milliseconds) {
        val testGuideKey = "test_guide_key"

		val guideFlow = repository.showGuideFlow(testGuideKey).stateIn(backgroundScope)
		assertTrue(guideFlow.value, "Guide should not be dismissed yet")

        repository.hideGuide(testGuideKey)

		assertFalse(guideFlow.filter { !it }.first(), "Guide should no longer show")
    }
}