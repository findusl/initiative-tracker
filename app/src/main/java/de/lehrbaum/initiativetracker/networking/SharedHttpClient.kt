package de.lehrbaum.initiativetracker.networking

import de.lehrbaum.initiativetracker.BuildConfig
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.plugins.websocket.wss
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.http.HttpMethod
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private const val TAG = "SharedHttpClient"

val sharedHttpClient = HttpClient(OkHttp) {
	install(WebSockets) {
		contentConverter = KotlinxWebsocketSerializationConverter(Json)
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
		level = LogLevel.INFO // change for debugging, lot of logs
	}
}

/**
 * Provides either a websocket via http or https based on BuildFlavor.
 * For more information see [HttpClient.webSocket]
 */
@Suppress("KotlinConstantConditions") // depends on build variant
public suspend fun HttpClient.buildConfigWebsocket(
	method: HttpMethod = HttpMethod.Get,
	host: String = BuildConfig.BACKEND_HOST,
	port: Int? = BuildConfig.BACKEND_PORT,
	path: String? = "/session",
	request: HttpRequestBuilder.() -> Unit = {},
	block: suspend DefaultClientWebSocketSession.() -> Unit
) {
	when (BuildConfig.FLAVOR) {
		"lan" -> {
			webSocket(method, host, port, path, request, block)
		}
		"remote" -> {
			wss(method, host, port, path, request, block)
		}
	}
}
