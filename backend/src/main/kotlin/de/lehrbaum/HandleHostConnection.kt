package de.lehrbaum

import de.lehrbaum.initiativetracker.commands.HostCommand
import de.lehrbaum.initiativetracker.commands.StartCommand
import io.ktor.server.websocket.DefaultWebSocketServerSession
import io.ktor.server.websocket.receiveDeserialized
import io.ktor.server.websocket.sendSerialized
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

suspend fun DefaultWebSocketServerSession.handleHostingCommand(hostingCommand: StartCommand.HostingCommand) {
	var session: Session? = null
	try {
		session = obtainSession(hostingCommand) ?: return

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
				sessions[session.id] = session.copy(hasActiveHost = false)
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

private suspend fun DefaultWebSocketServerSession.obtainSession(hostingCommand: StartCommand.HostingCommand): Session? {
	when (hostingCommand) {
		is StartCommand.StartHosting -> {
			val session = createSession(hostingCommand)
			val response = StartCommand.StartHosting.SessionStarted(session.id)
			sendSerialized(response as StartCommand.StartHosting.Response)
			return session
		}
		is StartCommand.JoinAsHost -> {
			var session: Session? = null
			val response: StartCommand.JoinAsHost.Response = synchronized(sessions) {
				val localSession = sessions[hostingCommand.sessionId]
				return@synchronized if (localSession == null) {
					StartCommand.JoinAsHost.SessionNotFound
				} else if (localSession.hasActiveHost) {
					StartCommand.JoinAsHost.SessionAlreadyHasHost
				} else {
					session = localSession
					sessions[hostingCommand.sessionId] = localSession.copy(hasActiveHost = true)
					StartCommand.JoinAsHost.JoinedAsHost(localSession.combatState.value)
				}
			}
			sendSerialized(response)
			return session
		}
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

private fun getAvailableRandomSessionId(): Int {
	while (true) {
		val sessionId = Random.nextInt(9999)
		if (!sessions.containsKey(sessionId))
			return sessionId
	}
}