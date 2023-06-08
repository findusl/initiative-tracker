package de.lehrbaum.initiativetracker.bl.data

import kotlin.random.Random

private const val APP_ID_KEY = "id"
private const val SETTINGS_NAME = "settings"

object GeneralSettingsRepository {
	private val settings = createSettingsFactory().create(SETTINGS_NAME)

	val installationId = run {
		var id = settings.getLongOrNull(APP_ID_KEY)
		if (id == null) {
			id = Random.nextLong()
			settings.putLong(APP_ID_KEY, id)
		}
		id
	}
}
