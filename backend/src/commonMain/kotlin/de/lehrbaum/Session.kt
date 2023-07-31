package de.lehrbaum

import de.lehrbaum.initiativetracker.commands.ServerToHostCommand
import de.lehrbaum.initiativetracker.dtos.CombatModel
import io.ktor.server.websocket.DefaultWebSocketServerSession
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow

internal data class Session(
	val id: Int,
	var hostWebsocketSession: DefaultWebSocketServerSession?,
	val combatState: MutableStateFlow<CombatModel>,
	val serverCommandQueue: Channel<Pair<ServerToHostCommand, (Boolean) -> Unit>> = Channel(capacity = Channel.RENDEZVOUS),
)
