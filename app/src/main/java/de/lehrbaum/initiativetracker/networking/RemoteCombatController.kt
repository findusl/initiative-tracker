package de.lehrbaum.initiativetracker.networking

import android.content.res.Resources.NotFoundException
import de.lehrbaum.initiativetracker.GlobalInstances
import de.lehrbaum.initiativetracker.commands.JoinSessionResponse
import de.lehrbaum.initiativetracker.commands.ServerToClientCommand
import de.lehrbaum.initiativetracker.commands.StartCommand
import de.lehrbaum.initiativetracker.dtos.CombatDTO
import io.github.aakira.napier.Napier
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.receiveDeserialized
import io.ktor.client.plugins.websocket.sendSerialized
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

private const val TAG = "RemoteCombatController"

