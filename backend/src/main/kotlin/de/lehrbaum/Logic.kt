package de.lehrbaum

import de.lehrbaum.initiativetracker.commands.HostCommand
import de.lehrbaum.initiativetracker.commands.ServerToHostCommand
import de.lehrbaum.initiativetracker.commands.StartCommand
import de.lehrbaum.initiativetracker.dtos.CombatDTO
import io.ktor.server.websocket.DefaultWebSocketServerSession
import io.ktor.server.websocket.receiveDeserialized
import io.ktor.server.websocket.sendSerialized
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.coroutines.CoroutineContext
import kotlin.random.Random

private val sessions = mutableMapOf<Int, Session>()

suspend fun DefaultWebSocketServerSession.handleWebsocketRequests() {
	val startCommand = receiveDeserialized<StartCommand>()

	when (startCommand) {
		is StartCommand.StartHosting -> handleStartHosting(startCommand)
		is StartCommand.JoinSession -> TODO()
	}
}

private suspend fun DefaultWebSocketServerSession.handleStartHosting(startHosting: StartCommand.StartHosting) {
	var session: Session? = null
	try {
		session = synchronized(sessions) {
			val sessionId = getAvailableRandomSessionId()
			val newSession = Session(sessionId, this, MutableStateFlow(startHosting.combatDTO))
			sessions[sessionId] = newSession
			newSession
		}
		sendSerialized(ServerToHostCommand.SessionStarted(session.id) as ServerToHostCommand)

		launch(session.coroutineContext) {
			for (outgoing in session.serverCommandQueue) {
				sendSerialized(outgoing)
			}
		}
		// blocking consume
		withContext(session.coroutineContext) {
			handleHostCommands(session)
		}
	} finally {
		session?.let {
			synchronized(sessions) {
				sessions.remove(session.id)
			}
			session.cancel()
		}
	}
}

private suspend fun DefaultWebSocketServerSession.handleHostCommands(session: Session) {
	while (true) {
		val message = receiveDeserialized<HostCommand>()
		when (message) {
			is HostCommand.CombatUpdatedCommand -> {
				println("Got Combat update ${message.combat}")
				session.combatState.value = message.combat
			}
		}
	}
}

private data class Session(
	val id: Int,
	val hostWebsocketSession: DefaultWebSocketServerSession?,
	val combatState: MutableStateFlow<CombatDTO>,
	val serverCommandQueue: Channel<ServerToHostCommand> = Channel(capacity = Channel.BUFFERED),
	override val coroutineContext: CoroutineContext = Dispatchers.IO + SupervisorJob(),
) : CoroutineScope

private fun getAvailableRandomSessionId(): Int {
	while (true) {
		val sessionId = Random.nextInt(9999)
		if (!sessions.containsKey(sessionId))
			return sessionId
	}
}