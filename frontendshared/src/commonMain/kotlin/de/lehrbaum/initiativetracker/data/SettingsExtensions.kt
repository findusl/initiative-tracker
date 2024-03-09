package de.lehrbaum.initiativetracker.data

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.Settings
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.serializer
import com.russhwolf.settings.serialization.decodeValue as decodeValueRequireSerializer
import com.russhwolf.settings.serialization.decodeValueOrNull as decodeValueOrNullRequireSerializer
import com.russhwolf.settings.serialization.encodeValue as encodeValueRequireSerializer

@ExperimentalSerializationApi
@ExperimentalSettingsApi
inline fun <reified T> Settings.encodeValue(
	key: String,
	value: T,
): Unit =
	encodeValueRequireSerializer(serializer<T>(), key, value)

@ExperimentalSerializationApi
@ExperimentalSettingsApi
inline fun <reified T> Settings.decodeValue(
	key: String,
	default: T,
): T =
	decodeValueRequireSerializer(serializer<T>(), key, default)

@ExperimentalSerializationApi
@ExperimentalSettingsApi
inline fun <reified T> Settings.decodeValueOrNull(
	key: String,
): T? =
	decodeValueOrNullRequireSerializer(serializer<T>(), key)

inline fun <reified T> Settings.getOrSet(
	key: String,
	getter: Settings.(String) -> T?,
	setter: Settings.(String, T) -> Unit,
	default: () -> T,
): T {
	return getter(key) ?: default().also { setter(key, it) }
}

inline fun Settings.getLongOrSet(key: String, default: () -> Long): Long =
	getOrSet(key, Settings::getLongOrNull, Settings::putLong, default)
