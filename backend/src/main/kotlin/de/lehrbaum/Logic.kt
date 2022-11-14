package de.lehrbaum

import de.lehrbaum.initiativetracker.commands.*
import de.lehrbaum.initiativetracker.dtos.CombatDTO
import io.ktor.server.websocket.DefaultWebSocketServerSession
import io.ktor.server.websocket.receiveDeserialized
import io.ktor.server.websocket.sendSerialized
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.random.Random

private val sessions = mutableMapOf<Int, Session>()

suspend fun DefaultWebSocketServerSession.handleWebsocketRequests() {
	val startCommand = receiveDeserialized<StartCommand>()

	when (startCommand) {
		is StartCommand.StartHosting -> handleStartHosting(startCommand)
		is StartCommand.JoinSession -> handleJoinSession(startCommand)
		else -> TODO("Handle joining session as host")
	}
}

private suspend fun DefaultWebSocketServerSession.handleJoinSession(joinSession: StartCommand.JoinSession) {
	val session = sessions[joinSession.sessionId]
	if (session == null) {
		sendSerialized(JoinSessionResponse.SessionNotFound as JoinSessionResponse)
		return
	}
	sendSerialized(JoinSessionResponse.JoinedSession(session.combatState.value) as JoinSessionResponse)

	try {
		launch {
			handleClientCommands(session)
		}

		session.combatState.collectLatest {
			sendSerialized(ServerToClientCommand.CombatUpdatedCommand(it) as ServerToClientCommand)
		}

	} catch (e: Exception) {
		println("client websocket failed somehow $e")
	}
	println("Finished client websocket connection")
}

private suspend fun DefaultWebSocketServerSession.handleClientCommands(session: Session) {
	try {
		while (true) {
			val message = receiveDeserialized<ClientCommand>()
			when (message) {
				is ClientCommand.AddCombatant -> TODO("Not yet implemented")
			}
		}
	} catch (closed: ClosedReceiveChannelException) {
		println("Client websocket closed.")
	} catch (e: Exception) {
		println("Client commands failed with $e")
	}
}

private suspend fun DefaultWebSocketServerSession.handleStartHosting(startHosting: StartCommand.StartHosting) {
	var session: Session? = null
	try {
		session = createSession(startHosting)
		sendSerialized(StartCommand.StartHosting.SessionStarted(session.id) as StartCommand.StartHosting.Response)

		launch {
			handleHostCommands(session)
		}

		for (outgoing in session.serverCommandQueue) {
			sendSerialized(outgoing)
		}
	} catch (e: Exception) {
		println("Server websocket failed somehow $e")
	} finally {
		session?.let {
			synchronized(sessions) {
				sessions.remove(session.id)
				println("Removed session ${session.id}")
			}
		}
	}
	println("Finished host websocket connection")
}

private suspend fun DefaultWebSocketServerSession.handleHostCommands(session: Session) {
	try {
		while (true) {
			val message = receiveDeserialized<HostCommand>()
			when (message) {
				is HostCommand.CombatUpdatedCommand -> {
					println("Got Combat update ${message.combat}")
					session.combatState.value = message.combat
				}
			}
		}
	} catch (closed: ClosedReceiveChannelException) {
		println("Host websocket closed.")
	} catch (e: Exception) {
		println("Handling Host commands failed $e")
	}
}

private fun DefaultWebSocketServerSession.createSession(startHosting: StartCommand.StartHosting): Session {
	synchronized(sessions) {
		val sessionId = getAvailableRandomSessionId()
		val newSession = Session(sessionId, this, MutableStateFlow(startHosting.combatDTO))
		sessions[sessionId] = newSession
		return newSession
	}
}

private data class Session(
	val id: Int,
	val hostWebsocketSession: DefaultWebSocketServerSession?,
	val combatState: MutableStateFlow<CombatDTO>,
	val serverCommandQueue: Channel<ServerToHostCommand> = Channel(capacity = Channel.BUFFERED),
)

private fun getAvailableRandomSessionId(): Int {
	while (true) {
		val sessionId = Random.nextInt(9999)
		if (!sessions.containsKey(sessionId))
			return sessionId
	}
}