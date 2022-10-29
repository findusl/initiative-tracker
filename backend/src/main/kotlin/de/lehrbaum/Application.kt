package de.lehrbaum

import de.lehrbaum.initiativetracker.commands.HostCommand
import de.lehrbaum.initiativetracker.commands.ServerToHostCommand
import de.lehrbaum.initiativetracker.commands.ServerToHostCommand.SessionStarted
import de.lehrbaum.initiativetracker.commands.StartCommand
import de.lehrbaum.initiativetracker.commands.StartCommand.JoinSession
import de.lehrbaum.initiativetracker.commands.StartCommand.StartHosting
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.path
import io.ktor.server.routing.routing
import io.ktor.server.websocket.*
import io.ktor.websocket.close
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import org.slf4j.event.Level
import kotlin.coroutines.CoroutineContext
import kotlin.random.Random

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

private val sessions = mutableMapOf<Int, Session>()

fun Application.main() {
	configureMonitoring()
	configureSerialization()
	configureSockets()
	configureRouting()
}

private fun Application.configureRouting() {
	routing {
		webSocket("/session") {
			val startCommand = receiveDeserialized<StartCommand>()

			when (startCommand) {
				is StartHosting -> {
					println("Got a start hosting command")
				}
				is JoinSession -> TODO()
			}
			close()
		}
	}
}

private suspend fun DefaultWebSocketServerSession.handleStartHosting() {
	var session: Session? = null
	try {
		session = synchronized(sessions) {
			val sessionId = getAvailableRandomSessionId()
			val newSession = Session(sessionId, this)
			sessions[sessionId] = newSession
			newSession
		}
		sendSerialized(SessionStarted(session.id) as ServerToHostCommand)
		withContext(session.coroutineContext) {
			handleHostCommands()
		}
	} finally {
		session?.cancel()
	}
}

private suspend fun DefaultWebSocketServerSession.handleHostCommands() {
	while (true) {
		val message = receiveDeserialized<HostCommand>()
		when (message) {
			is HostCommand.CombatUpdatedCommand -> TODO()
		}
	}
}

private data class Session(
	val id: Int,
	val hostWebsocketSession: DefaultWebSocketServerSession?,
	val clientWebsocketSessions: MutableList<DefaultWebSocketServerSession> = mutableListOf(),
	override val coroutineContext: CoroutineContext = Dispatchers.IO + SupervisorJob(),
) : CoroutineScope

private fun getAvailableRandomSessionId(): Int {
	while (true) {
		val sessionId = Random.nextInt(9999)
		if (!sessions.containsKey(sessionId))
			return sessionId
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