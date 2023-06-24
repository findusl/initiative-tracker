package de.lehrbaum

import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.path
import io.ktor.server.routing.delete
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.webSocket
import kotlinx.serialization.json.Json
import org.slf4j.event.Level

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.main() {
	configureMonitoring()
	configureSerialization()
	configureSockets()
	configureRouting()
}

private const val BASE_PATH = "/session"
internal const val SESSION_ID_PARAMETER = "sessionId"

private fun Application.configureRouting() {
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

private fun Application.configureSockets() {
	install(WebSockets) {
		contentConverter = KotlinxWebsocketSerializationConverter(Json)
		masking = false
	}
}

private fun Application.configureMonitoring() {
	install(CallLogging) {
		level = Level.INFO
		filter { call -> call.request.path().startsWith("/") }
	}
}

private fun Application.configureSerialization() {
	install(ContentNegotiation) {
		json()
	}
}