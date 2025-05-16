package de.lehrbaum.initiativetracker.data

import com.russhwolf.settings.ExperimentalSettingsApi
import de.lehrbaum.initiativetracker.BuildKonfig
import kotlinx.serialization.ExperimentalSerializationApi
import kotlin.random.Random

private const val APP_ID_KEY = "id"
private const val DEFAULT_BACKEND_KEY = "defaultBackend"
private const val OPENAI_API_KEY = "openaiApiKey"
private const val SETTINGS_NAME = "settings"

@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
class GeneralSettingsRepository {
	private val settings = createSettingsFactory().create(SETTINGS_NAME)

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

}

private fun BuildKonfig.defaultBackendUri(): BackendUri =
	BackendUri(backendSecure, backendHost, backendPort)
