package de.lehrbaum.initiativetracker.data

import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import java.util.prefs.Preferences

actual fun createSettingsFactory(): Settings.Factory =
	PreferencesSettings.Factory(Preferences.userRoot().node("de.lehrbaum.initiativetracker"))