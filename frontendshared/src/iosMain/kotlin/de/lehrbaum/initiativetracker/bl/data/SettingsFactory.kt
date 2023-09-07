package de.lehrbaum.initiativetracker.bl.data

import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings

actual fun createSettingsFactory(): Settings.Factory =
	NSUserDefaultsSettings.Factory()
