package de.lehrbaum

import de.lehrbaum.initiativetracker.commands.HostCommand
import de.lehrbaum.initiativetracker.commands.StartCommand
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

private val logger = KtorSimpleLogger("main.HostConnection")

suspend fun DefaultWebSocketServerSession.handleHostingCommand(hostingCommand: StartCommand.HostingCommand) {
	var session: Session? = null
	try {
		session = obtainSession(hostingCommand) ?: return
		logger.debug("Host connected ${session.id}")
		with(HostSessionState()) {
			val commandForwardingJob = launch {
				handleOutgoingCommands(session)
			}

			handleHostCommands(session)
			// maybe not necessary since it is the same scope, not sure
			commandForwardingJob.cancel("Websocket closed")
		}
	} catch (e: Exception) {
		logger.warn("Server websocket failed somehow $e")
	} finally {
		session?.let {
			session.hostWebsocketSession = null
		}
	}
	logger.info("Finished host websocket connection ${session?.id}")
}

context(HostSessionState)
private suspend fun DefaultWebSocketServerSession.handleOutgoingCommands(session: Session) {
	for (outgoing in session.serverCommandQueue) {
		var responded = false
		try {
			logger.trace("Sending out command $outgoing")
			sendSerialized(outgoing.first)
			logger.trace("Awaiting response")
			// in case of error on host site this can hang. but then again host could reconnect, should fix
			val response = commandResponse.receive()
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

context(HostSessionState)
private suspend fun DefaultWebSocketServerSession.handleHostCommands(session: Session) {
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
					commandResponse.send(message.accepted)
				}
			}
		}
	} catch (closed: ClosedReceiveChannelException) {
		logger.debug("Host websocket closed.")
	} catch (e: Exception) {
		logger.warn("Handling Host commands failed $e")
	}
}

private suspend fun DefaultWebSocketServerSession.obtainSession(hostingCommand: StartCommand.HostingCommand): Session? {
	when (hostingCommand) {
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
					localSession.hostWebsocketSession = this
					StartCommand.JoinAsHost.JoinedAsHost(localSession.combatState.value)
				}
			}
			sendSerialized(response)
			return session
		}
	}
}

private class HostSessionState(
	val commandResponse: Channel<Boolean> = Channel(capacity = RENDEZVOUS)
)
