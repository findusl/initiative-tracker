package de.lehrbaum.initiativetracker.networking

import de.lehrbaum.initiativetracker.commands.HostCommand
import de.lehrbaum.initiativetracker.commands.ServerToHostCommand
import de.lehrbaum.initiativetracker.commands.StartCommand
import de.lehrbaum.initiativetracker.dtos.CombatDTO
import de.lehrbaum.initiativetracker.logic.CombatController
import de.lehrbaum.initiativetracker.logic.CombatantModel
import io.github.aakira.napier.Napier
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.receiveDeserialized
import io.ktor.client.plugins.websocket.sendSerialized
import io.ktor.client.plugins.websocket.webSocket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

private const val TAG = "ShareCombatController"

class ShareCombatController(
	private val combatController: CombatController
) {
	private var collectionJob: Job? = null

	var sessionId: Int? = null

	val isSharing: Boolean
		get() = collectionJob != null

	@OptIn(FlowPreview::class)
	fun startSharing(parentScope: CoroutineScope) {
		if (collectionJob != null) return
		parentScope.launch {
			sharedHttpClient.webSocket(host = "10.0.2.2", port = 8080, path = "/session") {
				val currentCombatState = toCombatDTO(combatController.combatants.value, combatController.activeCombatantIndex.value)
				val startMessage = StartCommand.StartHosting(currentCombatState) as StartCommand
				this.sendSerialized(startMessage)
				val response = receiveDeserialized<ServerToHostCommand>() as ServerToHostCommand.SessionStarted
				sessionId = response.sessionId
				Napier.d("Got Session id $sessionId")
				parentScope.launch { receiveEvents() }
				combine(combatController.combatants, combatController.activeCombatantIndex, ::toCombatDTO)
					.debounce(500.milliseconds)
					.collectLatest {
						sendSerialized(HostCommand.CombatUpdatedCommand(it) as HostCommand)
					}
			}
			// this blocks until session is closed
			Napier.d("Session was closed $sessionId", tag = TAG)
		}
	}

	private suspend fun DefaultClientWebSocketSession.receiveEvents() {
		while (true) {
			val incoming = receiveDeserialized<ServerToHostCommand>()
			when (incoming) {
				is ServerToHostCommand.SessionStarted -> TODO("This shouldn't happen")
			}
		}
	}

	private fun toCombatDTO(combatants: Sequence<CombatantModel>, activeCombatantIndex: Int) =
		CombatDTO(activeCombatantIndex, combatants.map(CombatantModel::toDTO).toList())

	fun stopSharing() {
		collectionJob?.cancel()
		collectionJob = null
	}
}
