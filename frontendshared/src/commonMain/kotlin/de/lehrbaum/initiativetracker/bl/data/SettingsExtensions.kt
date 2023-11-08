package de.lehrbaum.initiativetracker.bl.data

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.Settings
import com.russhwolf.settings.serialization.decodeValue
import com.russhwolf.settings.serialization.encodeValue
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.serializer

@ExperimentalSerializationApi
@ExperimentalSettingsApi
inline fun <reified T> Settings.encodeValue(
	key: String,
	value: T,
): Unit =
	encodeValue(serializer(), key, value)

@ExperimentalSerializationApi
@ExperimentalSettingsApi
inline fun <reified T> Settings.decodeValue(
	key: String,
	default: T,
): T =
	decodeValue(serializer(), key, default)

