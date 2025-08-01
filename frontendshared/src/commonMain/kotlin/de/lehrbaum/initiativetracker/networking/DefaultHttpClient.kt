package de.lehrbaum.initiativetracker.networking

import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private const val TAG = "DefaultHttpClient"

fun createDefaultHttpClient() =
	// This needs changes for iOS
	createPlatformSpecificHttpClient {
		install(WebSockets) {
			contentConverter = KotlinxWebsocketSerializationConverter(Json)
		}
		install(ContentNegotiation) {
			json(
				Json {
					isLenient = true
					ignoreUnknownKeys = true
				},
			)
		}
		install(Logging) {
			logger = object : Logger {
				override fun log(message: String) {
					Napier.v(message, null, TAG)
				}
			}
			level = LogLevel.INFO // change when debugging
		}
	}

expect fun createPlatformSpecificHttpClient(initializationBlock: HttpClientConfig<*>.() -> Unit): HttpClient
