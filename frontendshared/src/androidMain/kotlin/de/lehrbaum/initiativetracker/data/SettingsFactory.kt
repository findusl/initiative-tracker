package de.lehrbaum.initiativetracker.data

import android.content.Context
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.SharedPreferencesSettings
import de.lehrbaum.initiativetracker.ContextHolder

actual fun createSettings(name: String): ObservableSettings {
	val fullName = "de.lehrbaum.initiativetracker.$name"
	val delegate = ContextHolder.appContext.getSharedPreferences(fullName, Context.MODE_PRIVATE)
	return SharedPreferencesSettings(delegate)
}
