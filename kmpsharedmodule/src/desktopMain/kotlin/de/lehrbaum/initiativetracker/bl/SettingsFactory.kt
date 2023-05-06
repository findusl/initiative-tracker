package de.lehrbaum.initiativetracker.bl

import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import java.util.prefs.Preferences

actual fun SettingsFactory(): Settings.Factory =
	PreferencesSettings.Factory(Preferences.userRoot().node("de.lehrbaum.initiativetracker"))