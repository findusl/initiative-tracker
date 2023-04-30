package de.lehrbaum.initiativetracker.networking

import de.lehrbaum.initiativetracker.BuildKonfig
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
suspend inline fun <R> HttpClient.buildConfigWebsocket(
    method: HttpMethod = HttpMethod.Get,
    host: String = BuildKonfig.backendHost,
    port: Int? = BuildKonfig.backendPort,
    path: String? = "/session",
    request: HttpRequestBuilder.() -> Unit = {},
    crossinline block: suspend DefaultClientWebSocketSession.() -> R
): R {
    val schema = when (BuildKonfig.environment) {
        "lan" -> "ws"
        "remote" -> "wss"
        else -> throw UnsupportedOperationException("Unsupported build environment ${BuildKonfig.environment}")
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
