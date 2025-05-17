package de.lehrbaum.initiativetracker.data

import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.ObservableSettings

actual fun createSettings(name: String): ObservableSettings =
	NSUserDefaultsSettings.Factory().create(name)
