package de.lehrbaum

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.path
import io.ktor.server.request.receiveText
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import org.slf4j.event.Level

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

private const val SESSION_ID_PATH_PARAMETER = "sessionId"

private val sessions = mutableMapOf<String, String>()

fun Application.main() {
	configureMonitoring()
	configureSerialization()
	routing {
		get("/session/{$SESSION_ID_PATH_PARAMETER}") {
			val sessionId = call.parameters[SESSION_ID_PATH_PARAMETER]
			val sessionData = sessions[sessionId]
			if (sessionData == null) {
				call.respond(HttpStatusCode.NotFound, "No session with id $sessionId")
			} else {
				call.respondText(sessionData, ContentType.Application.Json, HttpStatusCode.OK)
			}
		}
		post("/session/{$SESSION_ID_PATH_PARAMETER}") {
			val sessionId = call.parameters[SESSION_ID_PATH_PARAMETER]!!
			if (sessions.size > 10) sessions.clear() // just a precaution
			val sessionData = call.receiveText()
			sessions[sessionId] = sessionData
			call.respond(HttpStatusCode.Created)
		}
	}
}

fun Application.configureMonitoring() {
	install(CallLogging) {
		level = Level.INFO
		filter { call -> call.request.path().startsWith("/") }
	}
}

fun Application.configureSerialization() {
	install(ContentNegotiation) {
		json()
	}
}