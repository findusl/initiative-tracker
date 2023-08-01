package de.lehrbaum

import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.delete
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.webSocket
import kotlinx.serialization.json.Json

fun startBackend() {
	embeddedServer(CIO, port = 8080) {
		configureSerialization()
		configureSockets()
		configureRouting()
		platformSpecificSetup()
	}.start(wait = true)
}

private const val BASE_PATH = "/session"
internal const val SESSION_ID_PARAMETER = "sessionId"

internal fun Application.configureRouting() {
	routing {
		post(BASE_PATH) {
			call.handlePostRequest()
		}
		delete("$BASE_PATH/{$SESSION_ID_PARAMETER}") {
			call.handleDeleteRequest()
		}
		webSocket(BASE_PATH) {
			handleWebsocketRequests()
		}
	}
}

internal fun Application.configureSockets() {
	install(WebSockets) {
		contentConverter = KotlinxWebsocketSerializationConverter(Json)
		masking = false
	}
}

internal fun Application.configureSerialization() {
	install(ContentNegotiation) {
		json()
	}
}