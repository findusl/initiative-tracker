package de.lehrbaum

import de.lehrbaum.initiativetracker.commands.*
import io.ktor.server.websocket.DefaultWebSocketServerSession
import io.ktor.server.websocket.receiveDeserialized
import io.ktor.server.websocket.sendSerialized
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

suspend fun DefaultWebSocketServerSession.handleJoinSession(joinSession: StartCommand.JoinSession) {
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

private suspend fun DefaultWebSocketServerSession.handleClientCommands(session: Session) = try {
	while (true) {
		val message = receiveDeserialized<ClientCommand>()
		when (message) {
			is ClientCommand.AddCombatant -> {
				val command: ServerToHostCommand = ServerToHostCommand.AddCombatant(message.combatant)
				// This will only wait until the command is sent not until a response is received.
				// Should probably change this in the future.
				session.serverCommandQueue.send(command)
			}
		}
	}
} catch (closed: ClosedReceiveChannelException) {
	println("Client websocket closed.")
} catch (e: Exception) {
	println("Client commands failed with $e")
}