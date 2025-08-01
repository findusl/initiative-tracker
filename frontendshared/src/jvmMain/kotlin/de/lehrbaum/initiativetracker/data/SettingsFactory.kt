package de.lehrbaum.initiativetracker.data

import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.PreferencesSettings
import java.util.prefs.Preferences

actual fun createSettings(name: String): ObservableSettings {
	val rootPreferences = Preferences.userRoot().node("de.lehrbaum.initiativetracker")
	return PreferencesSettings.Factory(rootPreferences).create(name)
}
