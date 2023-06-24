package de.lehrbaum

import de.lehrbaum.initiativetracker.commands.StartCommand
import de.lehrbaum.initiativetracker.dtos.CombatModel
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.websocket.DefaultWebSocketServerSession
import io.ktor.server.websocket.receiveDeserialized
import io.ktor.websocket.CloseReason
import io.ktor.websocket.close
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.random.Random

internal val sessions = mutableMapOf<Int, Session>()

suspend fun DefaultWebSocketServerSession.handleWebsocketRequests() {
	val startCommand = receiveDeserialized<StartCommand>()

	when (startCommand) {
		is StartCommand.HostingCommand -> handleHostingCommand(startCommand)
		is StartCommand.JoinSession -> handleJoinSession(startCommand)
	}
}

suspend fun ApplicationCall.handlePostRequest() {
	val combat = receive<CombatModel>()
	val session = createSession(combat, hostWebsocketSession = null)
	respond(HttpStatusCode.Created, session.id)
}

suspend fun ApplicationCall.handleDeleteRequest() {
	val sessionId = parameters[SESSION_ID_PARAMETER]?.toInt()
	val removedElement = synchronized(sessions) {
		sessions.remove(sessionId)
	}
	if (removedElement != null) {
		removedElement.combatState.value = CombatModel(activeCombatantIndex = -1, emptyList())
		removedElement.hostWebsocketSession?.close(CloseReason(CloseReason.Codes.GOING_AWAY, "Session is deleted"))
		respond(HttpStatusCode.NoContent)
	} else {
		respond(HttpStatusCode.NotFound)
	}
}

internal fun createSession(combatModel: CombatModel, hostWebsocketSession: DefaultWebSocketServerSession?): Session {
	synchronized(sessions) {
		val sessionId = getAvailableRandomSessionId()
		val newSession = Session(sessionId, hostWebsocketSession, MutableStateFlow(combatModel))
		sessions[sessionId] = newSession
		return newSession
	}
}

internal fun getAvailableRandomSessionId(): Int {
	while (true) {
		val sessionId = Random.nextInt(9999)
		if (!sessions.containsKey(sessionId))
			return sessionId
	}
}
