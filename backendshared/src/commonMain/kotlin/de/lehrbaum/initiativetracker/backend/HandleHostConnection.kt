package de.lehrbaum.initiativetracker.backend

import de.lehrbaum.initiativetracker.dtos.commands.HostCommand
import de.lehrbaum.initiativetracker.dtos.commands.StartCommand
import de.lehrbaum.initiativetracker.dtos.commands.StartCommand.HostingCommand
import de.lehrbaum.initiativetracker.dtos.commands.StartCommand.JoinAsHostById
import io.ktor.server.websocket.DefaultWebSocketServerSession
import io.ktor.server.websocket.receiveDeserialized
import io.ktor.server.websocket.sendSerialized
import io.ktor.util.logging.KtorSimpleLogger
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.RENDEZVOUS
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.withLock

private val logger = KtorSimpleLogger("main.HostConnection")

suspend fun DefaultWebSocketServerSession.handleHostingCommand(hostingCommand: HostingCommand) {
	var session: Session? = null
	try {
		session = obtainSession(hostingCommand) ?: return
		logger.debug("Host connected ${session.id}")
		val state = HostSessionState()
		val commandForwardingJob = launch {
			handleOutgoingCommands(session, state)
		}

		handleHostCommands(session, state)
		// maybe not necessary since it is the same scope, not sure
		commandForwardingJob.cancel("Websocket closed")
	} catch (e: Exception) {
		logger.warn("Server websocket failed somehow $e")
	} finally {
		session?.let {
			session.hostWebsocketSession = null
		}
	}
	logger.info("Finished host websocket connection ${session?.id}")
}

private suspend fun DefaultWebSocketServerSession.handleOutgoingCommands(session: Session, state: HostSessionState) {
	for (outgoing in session.serverCommandQueue) {
		var responded = false
		try {
			logger.trace("Sending out command $outgoing")
			sendSerialized(outgoing.first)
			logger.trace("Awaiting response")
			// in case of error on host site this can hang. but then again host could reconnect, should fix
			val response = state.commandResponse.receive()
			logger.trace("Got command response $response")
			outgoing.second(response)
			responded = true
		} finally {
			if (!responded) {
				outgoing.second(false) // in case of cancellation or other exceptions
			}
		}
	}
}

private suspend fun DefaultWebSocketServerSession.handleHostCommands(session: Session, state: HostSessionState) {
	try {
		while (true) {
			val message = receiveDeserialized<HostCommand>()
			when (message) {
				is HostCommand.CombatUpdatedCommand -> {
					logger.trace("Got Combat update ${message.combat}")
					session.combatState.value = message.combat
				}

				is HostCommand.CommandCompleted -> {
					logger.trace("Got command completed ${message.accepted}")
					state.commandResponse.send(message.accepted)
				}
			}
		}
	} catch (closed: ClosedReceiveChannelException) {
		logger.debug("Host websocket closed.")
	} catch (e: Exception) {
		logger.warn("Handling Host commands failed $e")
	}
}

private suspend fun DefaultWebSocketServerSession.obtainSession(hostingCommand: HostingCommand): Session? {
	var session: Session? = null
	val response: HostingCommand.Response = sessionMutex.withLock {
		val localSession = when (hostingCommand) {
			is JoinAsHostById -> sessions[hostingCommand.sessionId]
			StartCommand.JoinDefaultSessionAsHost -> defaultSession
		}
		if (localSession == null) {
			HostingCommand.SessionNotFound
		} else if (localSession.hostWebsocketSession?.isActive == true) {
			HostingCommand.SessionAlreadyHasHost
		} else {
			session = localSession
			localSession.hostWebsocketSession = this
			HostingCommand.JoinedAsHost(localSession.combatState.value)
		}
	}
	sendSerialized(response)
	return session
}

private class HostSessionState(
	val commandResponse: Channel<Boolean> = Channel(capacity = RENDEZVOUS),
)
