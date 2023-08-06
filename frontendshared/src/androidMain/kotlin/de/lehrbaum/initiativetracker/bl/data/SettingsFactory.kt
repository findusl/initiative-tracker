package de.lehrbaum.initiativetracker.bl.data

import android.content.Context
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import de.lehrbaum.initiativetracker.ContextHolder

actual fun createSettingsFactory(): Settings.Factory =
	object: Settings.Factory {
		override fun create(name: String?): Settings {
			val suffix = name ?: "preferences"
			val fullName = "de.lehrbaum.initiativetracker.$suffix"
			val delegate = ContextHolder.appContext.getSharedPreferences(fullName, Context.MODE_PRIVATE)
			return SharedPreferencesSettings(delegate)
		}
	}
