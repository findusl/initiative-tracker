package de.lehrbaum.initiativetracker.networking

import de.lehrbaum.initiativetracker.BuildConfig
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
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlin.coroutines.resume
import kotlin.time.Duration.Companion.milliseconds

private const val TAG = "ShareCombatController"

class ShareCombatController(
	private val combatController: CombatController
) {
	private var sharingJob: Job? = null

	var sessionId: Int? = null

	val isSharing: Boolean
		get() = sharingJob != null

	suspend fun startSharing(parentScope: CoroutineScope): Int {
		// TODO fast clicking could crash the app here
		if (sharingJob != null) return sessionId ?: throw IllegalStateException("Sharing but no sessionId?")

		return suspendCancellableCoroutine { continuation ->
			sharingJob = parentScope.launch {
				sharedHttpClient.webSocket(host = BuildConfig.BACKEND_HOST, port = BuildConfig.BACKEND_PORT, path = "/session") {
					val currentCombatState = toCombatDTO(combatController.combatants.value, combatController.activeCombatantIndex.value)
					val startMessage = StartCommand.StartHosting(currentCombatState) as StartCommand
					this.sendSerialized(startMessage)
					val response = receiveDeserialized<ServerToHostCommand>() as ServerToHostCommand.SessionStarted
					Napier.d("Got Session id $sessionId")
					sessionId = response.sessionId
					continuation.resume(response.sessionId)

					parentScope.launch { receiveEvents() }
					shareCombatUpdates()
				}
				// this blocks until session is closed
				Napier.d("Session was closed $sessionId", tag = TAG)
			}
		}
	}

	@OptIn(FlowPreview::class)
	private suspend fun DefaultClientWebSocketSession.shareCombatUpdates() {
		combine(combatController.combatants, combatController.activeCombatantIndex, ::toCombatDTO)
			.debounce(200.milliseconds)
			.collectLatest {
				sendSerialized(HostCommand.CombatUpdatedCommand(it) as HostCommand)
			}
	}

	private suspend fun DefaultClientWebSocketSession.receiveEvents() {
		while (true) {
			val incoming = receiveDeserialized<ServerToHostCommand>()
			when (incoming) {
				is ServerToHostCommand.SessionStarted -> TODO("Change to be separate response")
			}
		}
	}

	private fun toCombatDTO(combatants: Sequence<CombatantModel>, activeCombatantIndex: Int) =
		CombatDTO(activeCombatantIndex, combatants.map(CombatantModel::toDTO).toList())

	fun stopSharing() {
		sharingJob?.cancel()
		sharingJob = null
	}
}
