package de.lehrbaum.initiativetracker.networking

import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import java.time.Duration

private const val TAG = "DefaultHttpClient"
private val WEBSOCKET_PING_INTERVAL = Duration.ofSeconds(20)

fun createDefaultHttpClient() =
	// This needs changes for iOS
    HttpClient(OkHttp) {
		engine {
			preconfigured = OkHttpClient.Builder()
				.pingInterval(WEBSOCKET_PING_INTERVAL)
				.build()
		}
        install(WebSockets) {
            contentConverter = KotlinxWebsocketSerializationConverter(Json)
			pingInterval = WEBSOCKET_PING_INTERVAL.toMillis()
        }
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
            level = LogLevel.INFO // change when debugging
        }
    }