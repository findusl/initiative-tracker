package de.lehrbaum.initiativetracker.networking

import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private const val TAG = "SharedHttpClient"

val sharedHttpClient = HttpClient(Android) {
	install(ContentNegotiation) {
		json(Json {
			isLenient = true
			ignoreUnknownKeys = true
		})
	}
	install(Logging) {
		logger = object : Logger {
			override fun log(message: String) {
				Napier.i(message, null, TAG)
			}
		}
		level = LogLevel.NONE // change for debugging, lot of logs
	}
}