package de.lehrbaum.initiativetracker.networking

import de.lehrbaum.initiativetracker.BuildKonfig
import de.lehrbaum.initiativetracker.bl.data.CombatLink
import io.ktor.client.HttpClient
import io.ktor.client.plugins.plugin
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.prepareRequest
import io.ktor.client.request.url
import io.ktor.http.HttpMethod
import io.ktor.websocket.close
import kotlinx.coroutines.cancel

/**
 * Provides either a websocket via http or https based on BuildFlavor.
 * For more information see [HttpClient.webSocket].
 *
 * The websocket coroutineContext is cancelled once the block finishes. Weirdly not standard behaviour.
 */
suspend inline fun <R> HttpClient.buildBackendWebsocket(
    method: HttpMethod = HttpMethod.Get,
    path: String = SESSION_PATH,
    request: HttpRequestBuilder.() -> Unit = {},
    crossinline block: suspend DefaultClientWebSocketSession.() -> R
): R {
	// TODO handle different backends from combatLink
    plugin(WebSockets)
    val session = prepareRequest {
        this.method = method
		backendWebsocketUrl(path)
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

fun HttpRequestBuilder.backendWebsocketUrl(path: String) {
	val scheme = when (BuildKonfig.environment) {
		"lan" -> "ws"
		"remote" -> "wss"
		else -> throw UnsupportedOperationException("Unsupported build environment ${BuildKonfig.environment}")
	}
	url(scheme, BuildKonfig.backendHost, BuildKonfig.backendPort, path)
}

fun HttpRequestBuilder.backendHttpUrl(path: String) {
	val scheme = when (BuildKonfig.environment) {
		"lan" -> "http"
		"remote" -> "https"
		else -> throw UnsupportedOperationException("Unsupported build environment ${BuildKonfig.environment}")
	}
	url(scheme, BuildKonfig.backendHost, BuildKonfig.backendPort, path)
}

fun HttpRequestBuilder.backendHttpUrl(combatLink: CombatLink, path: String) {
	val scheme = if (combatLink.secureConnection) "https" else "http"
	// TODO test when host contains a path
	url(scheme, combatLink.host, combatLink.port, path)
}


