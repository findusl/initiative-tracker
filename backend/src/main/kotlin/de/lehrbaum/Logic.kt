package de.lehrbaum

import de.lehrbaum.initiativetracker.commands.StartCommand
import io.ktor.server.websocket.DefaultWebSocketServerSession
import io.ktor.server.websocket.receiveDeserialized

internal val sessions = mutableMapOf<Int, Session>()

suspend fun DefaultWebSocketServerSession.handleWebsocketRequests() {
	val startCommand = receiveDeserialized<StartCommand>()

	when (startCommand) {
		is StartCommand.HostingCommand -> handleHostingCommand(startCommand)
		is StartCommand.JoinSession -> handleJoinSession(startCommand)
	}
}
