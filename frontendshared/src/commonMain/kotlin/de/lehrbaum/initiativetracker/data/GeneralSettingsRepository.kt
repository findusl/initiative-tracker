package de.lehrbaum.initiativetracker.data

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import de.lehrbaum.initiativetracker.BuildKonfig
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.serialization.ExperimentalSerializationApi
import kotlin.random.Random

private const val APP_ID_KEY = "id"
private const val DEFAULT_BACKEND_KEY = "defaultBackend"
private const val OPENAI_API_KEY = "openaiApiKey"
private const val HOMEBREW_LINKS_KEY = "homebrewLinks"
private const val SETTINGS_NAME = "settings"
private const val SHOW_GUIDE_PREFIX = "showGuide"

@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
class GeneralSettingsRepository(
	private val settings: ObservableSettings = createSettings(SETTINGS_NAME)
) {

	private var showGuideDefault = true

	val installationId = settings.getLongOrSet(APP_ID_KEY, Random::nextLong)

	var defaultBackendUri: BackendUri =
		settings.decodeValue(DEFAULT_BACKEND_KEY, BuildKonfig.defaultBackendUri())
		set(value) {
			field = value
			settings.encodeValue(DEFAULT_BACKEND_KEY, value)
		}

	var openAiApiKey: String? = settings.getStringOrNull(OPENAI_API_KEY)
		set(value) {
			field = value
			if (value != null) {
				settings.putString(OPENAI_API_KEY, value)
			} else {
				settings.remove(OPENAI_API_KEY)
			}
		}

	private val defaultHomebrewLinks = listOf(
		"https://raw.githubusercontent.com/TheGiddyLimit/homebrew/master/creature/Kobold%20Press%3B%20Tome%20of%20Beasts.json",
		"https://raw.githubusercontent.com/TheGiddyLimit/homebrew/master/creature/Kobold%20Press%3B%20Tome%20of%20Beasts%202.json",
		"https://raw.githubusercontent.com/TheGiddyLimit/homebrew/master/creature/Kobold%20Press%3B%20Tome%20of%20Beasts%203.json"
	)

	private fun loadHomebrewLinks(): List<String> {
		return settings.decodeValue(HOMEBREW_LINKS_KEY, defaultHomebrewLinks)
	}

	private val homebrewLinksStateFlow = MutableStateFlow(loadHomebrewLinks())
	val homebrewLinksFlow: Flow<List<String>> = homebrewLinksStateFlow

	var homebrewLinks: List<String> = loadHomebrewLinks()
		set(value) {
			field = value
			homebrewLinksStateFlow.value = value
			settings.encodeValue(HOMEBREW_LINKS_KEY, value)
		}

	fun showGuideFlow(guideKey: String): Flow<Boolean> {
		return callbackFlow {
			val key = "$SHOW_GUIDE_PREFIX$guideKey"
			trySend(settings.getBooleanOrSet(key, { showGuideDefault }))
			val listener = settings.addBooleanListener(key, defaultValue = true) { value ->
				trySend(value)
			}
			awaitClose { listener.deactivate() }
		}.conflate()
	}

	fun hideGuide(guideKey: String) {
		settings.putBoolean("$SHOW_GUIDE_PREFIX$guideKey", false)
	}

	fun hideAllGuides() {
		applyToAllGuides(false)
	}

	fun showAllGuides() {
		applyToAllGuides(true)
	}

	private fun applyToAllGuides(value: Boolean) {
		showGuideDefault = value
		settings.keys.forEach { key ->
			if (key.startsWith(SHOW_GUIDE_PREFIX)) {
				settings.putBoolean(key, value)
			}
		}
	}
}

private fun BuildKonfig.defaultBackendUri(): BackendUri =
	BackendUri(backendSecure, backendHost, backendPort)
