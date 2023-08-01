package de.lehrbaum

import de.lehrbaum.initiativetracker.commands.*
import io.ktor.server.websocket.DefaultWebSocketServerSession
import io.ktor.server.websocket.receiveDeserialized
import io.ktor.server.websocket.sendSerialized
import io.ktor.util.logging.KtorSimpleLogger
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.flow.collectLatest

private val logger = KtorSimpleLogger("main.ClientConnection")

suspend fun DefaultWebSocketServerSession.handleJoinSession(joinSession: StartCommand.JoinSession) {
	val session = sessions[joinSession.sessionId]
	if (session == null) {
		sendSerialized(JoinSessionResponse.SessionNotFound as JoinSessionResponse)
		return
	}
	logger.debug("Client connected session ${session.id}")
	sendSerialized(JoinSessionResponse.JoinedSession(session.combatState.value) as JoinSessionResponse)

	try {
		val state = ClientSessionState()
		launch {
			handleClientCommands(session, state)
		}

		// This will stop once the session ends
		session.combatState.collectLatest {
			sendSerialized(ServerToClientCommand.CombatUpdatedCommand(it) as ServerToClientCommand)
		}

	} catch (e: Exception) {
		logger.warn("client websocket failed somehow $e")
	}
	logger.info("Finished client websocket connection for session ${session.id}")
}

private suspend fun DefaultWebSocketServerSession.handleClientCommands(session: Session, state: ClientSessionState) {
	try {
		while (true) {
			val message = receiveDeserialized<ClientCommand>()
			when (message) {
				is ClientCommand.AddCombatant -> {
					logger.trace("Got addCombatant command $message")
					val command: ServerToHostCommand = ServerToHostCommand.AddCombatant(message.combatant)
					forwardCommandAndHandleResponse(session, command, state)
				}

				is ClientCommand.EditCombatant -> {
					logger.trace("Got editCombatant command $message")
					val command: ServerToHostCommand = ServerToHostCommand.EditCombatant(message.combatant)
					forwardCommandAndHandleResponse(session, command, state)
				}
				is ClientCommand.DamageCombatant -> {
					logger.trace("Got damageCombatant command $message")
					val command: ServerToHostCommand =
						ServerToHostCommand.DamageCombatant(message.combatantId, message.damage, message.ownerId)
					forwardCommandAndHandleResponse(session, command, state)
				}

				ClientCommand.CancelCommand -> {
					// don't wait here. Either there is an active command or not.
					logger.debug("Got Cancel. Active scope is ${state.activeCommandScope}")
					state.activeCommandScope?.cancel()
				}
			}
		}
	} catch (closed: ClosedReceiveChannelException) {
		logger.debug("Client websocket closed.")
	} catch (e: Exception) {
		logger.warn("Client commands failed with $e")
	}
}

private suspend fun DefaultWebSocketServerSession.forwardCommandAndHandleResponse(
	session: Session,
	command: ServerToHostCommand,
	state: ClientSessionState
) = with(state) {
	require(activeCommandScope == null) { "This client tried to send two commands!" }
	activeCommandScope = plus(Job()) // avoids cancelling the whole outer scope on exception
	activeCommandScope?.launch {
		val scopeReference = activeCommandScope // Keep the scope to launch the response if it was not cancelled
		val onComplete: (Boolean) -> Unit = { result: Boolean ->
			logger.trace("Lambda command result $result")
			scopeReference?.launch {
				logger.trace("Sending result to client $result")
				sendSerialized(ServerToClientCommand.CommandCompleted(result) as ServerToClientCommand)
			}
		}
		logger.trace("Sending to server command queue")
		session.serverCommandQueue.send(Pair(command, onComplete))
		// Maybe a more object oriented approach could improve this. Then we could call a suspend function here that
		// will complete completely or throw.
	}?.invokeOnCompletion {
		activeCommandScope = null  // Once the command is sent there is no cancelling anymore
	}
}

private class ClientSessionState(
	var activeCommandScope: CoroutineScope? = null
)
