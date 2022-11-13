package de.lehrbaum.initiativetracker.networking

import android.content.res.Resources.NotFoundException
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

class RemoteCombatController(private val sessionId: Int) {

	val remoteCombat = flow {
		sharedHttpClient.buildConfigWebsocket {
			initiateClient()
			handleUpdates()
		}
		Napier.i("Finished Websocket in remote combat", tag = TAG)
	}
		.flowOn(Dispatchers.IO)

	context(FlowCollector<CombatDTO>, DefaultClientWebSocketSession)
		private suspend fun initiateClient() {
		val joinSessionRequest = StartCommand.JoinSession(sessionId) as StartCommand
		sendSerialized(joinSessionRequest)
		val response = receiveDeserialized<JoinSessionResponse>()
		when (response) {
			is JoinSessionResponse.JoinedSession -> emit(response.combatDTO)
			JoinSessionResponse.SessionNotFound -> throw NotFoundException("Could not find session $sessionId")
		}
	}

	context(FlowCollector<CombatDTO>, DefaultClientWebSocketSession)
		private suspend fun handleUpdates() {
		while (true) {
			val message = receiveDeserialized<ServerToClientCommand>()

			when (message) {
				is ServerToClientCommand.CombatUpdatedCommand -> emit(message.combat)
			}
		}
	}
}
