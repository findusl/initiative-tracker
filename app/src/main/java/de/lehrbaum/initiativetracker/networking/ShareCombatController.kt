package de.lehrbaum.initiativetracker.networking

import de.lehrbaum.initiativetracker.commands.HostCommand
import de.lehrbaum.initiativetracker.commands.ServerToHostCommand
import de.lehrbaum.initiativetracker.commands.StartCommand
import de.lehrbaum.initiativetracker.dtos.CombatDTO
import de.lehrbaum.initiativetracker.logic.CombatController
import de.lehrbaum.initiativetracker.logic.CombatantModel
import io.github.aakira.napier.Napier
import io.ktor.client.plugins.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlin.time.Duration.Companion.milliseconds

@Suppress("unused")
private const val TAG = "ShareCombatController"

class ShareCombatController(
	private val combatController: CombatController
) {
	var sessionId = MutableStateFlow<Int?>(null)

	suspend fun joinCombatAsHost(sessionId: Int) {
		sharedHttpClient.buildConfigWebsocket {
			joinSessionAsHost(sessionId)
			launch { receiveEvents() }
			shareCombatUpdates()
		}
	}

	private suspend fun DefaultClientWebSocketSession.joinSessionAsHost(sessionId: Int) {
		val startMessage = StartCommand.JoinAsHost(sessionId) as StartCommand
		this.sendSerialized(startMessage)
		val response = receiveDeserialized<StartCommand.JoinAsHost.Response>()
		when (response) {
			is StartCommand.JoinAsHost.JoinedAsHost -> {
				val combatState = response.combatDTO
				TODO("Update combat controller with new ")

			}
			StartCommand.JoinAsHost.SessionAlreadyHasHost -> {
				TODO("Throw some exception and show to user")
			}
		}
	}

	suspend fun shareCombatState() {
		sharedHttpClient.buildConfigWebsocket {
			startHostingSession()
			launch { receiveEvents() }
			shareCombatUpdates()
		}
	}

	private suspend fun DefaultClientWebSocketSession.startHostingSession() {
		val currentCombatState = toCombatDTO(combatController.combatants.value, combatController.activeCombatantIndex.value)
		val startMessage = StartCommand.StartHosting(currentCombatState) as StartCommand
		this.sendSerialized(startMessage)
		val response = receiveDeserialized<StartCommand.StartHosting.Response>() as StartCommand.StartHosting.SessionStarted
		Napier.d("Got Session id $sessionId")
		sessionId.value = response.sessionId
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
			TODO("No commands to handle yet")
		}
	}

	private fun toCombatDTO(combatants: Sequence<CombatantModel>, activeCombatantIndex: Int) =
		CombatDTO(activeCombatantIndex, combatants.map(CombatantModel::toDTO).toList())
}
