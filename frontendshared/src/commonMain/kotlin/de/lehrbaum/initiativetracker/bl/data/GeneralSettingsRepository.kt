package de.lehrbaum.initiativetracker.bl.data

import com.russhwolf.settings.ExperimentalSettingsApi
import de.lehrbaum.initiativetracker.BuildKonfig
import kotlinx.serialization.ExperimentalSerializationApi
import kotlin.random.Random

private const val APP_ID_KEY = "id"
private const val DEFAULT_BACKEND_KEY = "defaultBackend"
private const val SETTINGS_NAME = "settings"

@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
class GeneralSettingsRepository {
	private val settings = createSettingsFactory().create(SETTINGS_NAME)

	val installationId = settings.getLongOrSet(APP_ID_KEY, Random::nextLong)

	var defaultBackend: Backend =
		settings.decodeValue(DEFAULT_BACKEND_KEY, BuildKonfig.defaultBackend())
		set(value) {
			field = value
			settings.encodeValue(DEFAULT_BACKEND_KEY, value)
		}

}

private fun BuildKonfig.defaultBackend(): Backend =
	Backend(backendSecure, backendHost, backendPort)
