package de.lehrbaum

import de.lehrbaum.initiativetracker.commands.*
import io.ktor.server.websocket.DefaultWebSocketServerSession
import io.ktor.server.websocket.receiveDeserialized
import io.ktor.server.websocket.sendSerialized
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.flow.collectLatest

suspend fun DefaultWebSocketServerSession.handleJoinSession(joinSession: StartCommand.JoinSession) {
	val session = sessions[joinSession.sessionId]
	if (session == null) {
		sendSerialized(JoinSessionResponse.SessionNotFound as JoinSessionResponse)
		return
	}
	sendSerialized(JoinSessionResponse.JoinedSession(session.combatState.value) as JoinSessionResponse)

	try {
		with(ClientSessionState()) {
			launch {
				handleClientCommands(session)
			}

			// This will stop once the session ends
			session.combatState.collectLatest {
				sendSerialized(ServerToClientCommand.CombatUpdatedCommand(it) as ServerToClientCommand)
			}
		}

	} catch (e: Exception) {
		println("client websocket failed somehow $e")
	}
	println("Finished client websocket connection")
}

context(ClientSessionState)
private suspend fun DefaultWebSocketServerSession.handleClientCommands(session: Session) {
	try {
		while (true) {
			val message = receiveDeserialized<ClientCommand>()
			when (message) {
				is ClientCommand.AddCombatant -> {
					println("Got addCombatant command $message")
					val command: ServerToHostCommand = ServerToHostCommand.AddCombatant(message.combatant)
					forwardCommandAndHandleResponse(session, command)
				}

				ClientCommand.CancelCommand -> {
					// don't wait here. Either there is an active command or not.
					println("Got Cancel. Active scope is $activeCommandScope")
					activeCommandScope?.cancel()
				}
			}
		}
	} catch (closed: ClosedReceiveChannelException) {
		println("Client websocket closed.")
	} catch (e: Exception) {
		println("Client commands failed with $e")
	}
}

context(ClientSessionState)
private suspend fun DefaultWebSocketServerSession.forwardCommandAndHandleResponse(session: Session, command: ServerToHostCommand) {
	assert(activeCommandScope == null) { "This client tried to send two commands!" }
	activeCommandScope = plus(Job()) // avoids cancelling the whole outer scope on exception
	activeCommandScope?.launch {
		val scopeReference = activeCommandScope // Keep the scope to launch the response if it was not cancelled
		val onComplete: (Boolean) -> Unit = { result: Boolean ->
			println("Lambda command result $result")
			scopeReference?.launch {
				println("Sending result to client $result")
				sendSerialized(ServerToClientCommand.CommandCompleted(result) as ServerToClientCommand)
			}
		}
		println("Sending to server command queue")
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
