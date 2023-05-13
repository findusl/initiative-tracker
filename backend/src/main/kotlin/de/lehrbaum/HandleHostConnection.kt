package de.lehrbaum

import de.lehrbaum.initiativetracker.commands.HostCommand
import de.lehrbaum.initiativetracker.commands.StartCommand
import io.ktor.server.websocket.DefaultWebSocketServerSession
import io.ktor.server.websocket.receiveDeserialized
import io.ktor.server.websocket.sendSerialized
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

suspend fun DefaultWebSocketServerSession.handleHostingCommand(hostingCommand: StartCommand.HostingCommand) {
	var session: Session? = null
	try {
		session = obtainSession(hostingCommand) ?: return

		val commandForwardingJob = launch {
			for (outgoing in session.serverCommandQueue) {
				sendSerialized(outgoing)
			}
		}

		handleHostCommands(session)
		// maybe not necessary since it is the same scope, not sure
		commandForwardingJob.cancel("Websocket closed")
	} catch (e: Exception) {
		println("Server websocket failed somehow $e")
	} finally {
		session?.let {
			synchronized(sessions) {
				sessions[session.id] = session.copy(hostWebsocketSession = null)
			}
		}
	}
	println("Finished host websocket connection ${session?.id}")
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
			val session = createSession(hostingCommand.combatDTO, hostWebsocketSession = this)
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
				} else if (localSession.hostWebsocketSession?.isActive == true) {
					StartCommand.JoinAsHost.SessionAlreadyHasHost
				} else {
					session = localSession
					sessions[hostingCommand.sessionId] = localSession.copy(hostWebsocketSession = this)
					StartCommand.JoinAsHost.JoinedAsHost(localSession.combatState.value)
				}
			}
			sendSerialized(response)
			return session
		}
	}
}
