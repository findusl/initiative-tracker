package de.lehrbaum.initiativetracker.networking

import de.lehrbaum.initiativetracker.bl.data.Backend
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
	backend: Backend,
    method: HttpMethod = HttpMethod.Get,
    path: String = SESSION_PATH,
    request: HttpRequestBuilder.() -> Unit = {},
    crossinline block: suspend DefaultClientWebSocketSession.() -> R
): R {
    plugin(WebSockets)
    val session = prepareRequest {
        this.method = method
		backendWebsocketUrl(backend, path)
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

fun HttpRequestBuilder.backendWebsocketUrl(backend: Backend, path: String) {
	val scheme = if(backend.secureConnection) "wss" else "ws"
	url(scheme, backend.hostUrl, backend.port, path)
}

fun HttpRequestBuilder.backendHttpUrl(backend: Backend, path: String) {
	val scheme = if (backend.secureConnection) "https" else "http"
	url(scheme, backend.hostUrl, backend.port, path)
}


