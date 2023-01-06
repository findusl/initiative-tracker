package de.lehrbaum

import de.lehrbaum.initiativetracker.commands.ServerToHostCommand
import de.lehrbaum.initiativetracker.dtos.CombatDTO
import io.ktor.server.websocket.DefaultWebSocketServerSession
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow

internal data class Session(
	val id: Int,
	val hostWebsocketSession: DefaultWebSocketServerSession?,
	val combatState: MutableStateFlow<CombatDTO>,
	val hasActiveHost: Boolean = true,
	val serverCommandQueue: Channel<ServerToHostCommand> = Channel(capacity = Channel.RENDEZVOUS),
)
