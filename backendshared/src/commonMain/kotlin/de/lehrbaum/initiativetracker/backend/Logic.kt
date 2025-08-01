package de.lehrbaum.initiativetracker.backend

import de.lehrbaum.initiativetracker.dtos.CombatModel
import de.lehrbaum.initiativetracker.dtos.commands.StartCommand
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.websocket.DefaultWebSocketServerSession
import io.ktor.server.websocket.receiveDeserialized
import io.ktor.util.logging.KtorSimpleLogger
import io.ktor.websocket.CloseReason
import io.ktor.websocket.close
import kotlin.random.Random
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

private val logger = KtorSimpleLogger("main.Logic")

internal val defaultSession: Session? = null
internal val sessions = mutableMapOf<Int, Session>()
internal val sessionMutex = Mutex()

suspend fun DefaultWebSocketServerSession.handleWebsocketRequests() {
	val startCommand = receiveDeserialized<StartCommand>()

	when (startCommand) {
		is StartCommand.HostingCommand -> handleHostingCommand(startCommand)
		is StartCommand.JoiningCommand -> handleJoinSession(startCommand)
	}
}

suspend fun ApplicationCall.handlePostRequest() {
	val combat = receive<CombatModel>()
	val session = createSession(combat, hostWebsocketSession = null)
	logger.info("Created session $session")
	respond(HttpStatusCode.Created, session.id)
}

suspend fun ApplicationCall.handleDeleteRequest() {
	val sessionId = parameters[SESSION_ID_PARAMETER]?.toInt()
	val removedElement = sessionMutex.withLock {
		sessions.remove(sessionId)
	}
	if (removedElement != null) {
		logger.info("Deleted session with id $sessionId")
		removedElement.hostWebsocketSession?.close(CloseReason(CloseReason.Codes.GOING_AWAY, "Session is deleted"))
		respond(HttpStatusCode.NoContent)
	} else {
		respond(HttpStatusCode.NotFound)
	}
}

internal suspend fun createSession(combatModel: CombatModel, hostWebsocketSession: DefaultWebSocketServerSession?): Session =
	sessionMutex.withLock {
		val sessionId = getAvailableRandomSessionId()
		val newSession = Session(sessionId, hostWebsocketSession, MutableStateFlow(combatModel))
		sessions[sessionId] = newSession
		newSession
	}

internal fun getAvailableRandomSessionId(): Int {
	while (true) {
		val sessionId = Random.nextInt(10000)
		if (!sessions.containsKey(sessionId)) {
			return sessionId
		}
	}
}
