package de.lehrbaum.initiativetracker.data

import com.russhwolf.settings.Settings

/**
 * In this app the SettingsFactories prefix every settings with de.lehrbaum.initiativetracker
 */
expect fun createSettingsFactory(): Settings.Factory
