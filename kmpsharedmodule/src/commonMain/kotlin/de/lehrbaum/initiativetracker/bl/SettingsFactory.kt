package de.lehrbaum.initiativetracker.bl

import com.russhwolf.settings.Settings

/**
 * In this app the SettingsFactories prefix every settings with de.lehrbaum.initiativetracker
 */
expect fun SettingsFactory(): Settings.Factory
