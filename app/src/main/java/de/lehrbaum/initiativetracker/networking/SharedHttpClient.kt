package de.lehrbaum.initiativetracker.networking

import de.lehrbaum.initiativetracker.BuildConfig
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.plugin
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.prepareRequest
import io.ktor.client.request.url
import io.ktor.http.HttpMethod
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.serialization.kotlinx.json.json
import io.ktor.websocket.close
import kotlinx.coroutines.cancel
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
 * For more information see [HttpClient.webSocket].
 *
 * The websocket coroutineContext is cancelled once the block finishes. Weirdly not standard behaviour.
 */
@Suppress("KotlinConstantConditions") // depends on build variant
suspend inline fun <R> HttpClient.buildConfigWebsocket(
	method: HttpMethod = HttpMethod.Get,
	host: String = BuildConfig.BACKEND_HOST,
	port: Int? = BuildConfig.BACKEND_PORT,
	path: String? = "/session",
	request: HttpRequestBuilder.() -> Unit = {},
	crossinline block: suspend DefaultClientWebSocketSession.() -> R
): R {
	val schema = when (BuildConfig.FLAVOR) {
		"lan" -> "ws"
		"remote" -> "wss"
		else -> throw UnsupportedOperationException("Unsupported build flavor ${BuildConfig.FLAVOR}")
	}

	plugin(WebSockets)
	val session = prepareRequest {
		this.method = method
		url(schema, host, port, path)
		request()
	}

	return session.body<DefaultClientWebSocketSession, R> {
		try {
			block(it)
		} finally {
			it.close()
			it.cancel("Websocket closed")
		}
	}
}
