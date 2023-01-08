package de.lehrbaum.initiativetracker.networking

import de.lehrbaum.initiativetracker.commands.HostCommand
import de.lehrbaum.initiativetracker.commands.ServerToHostCommand
import de.lehrbaum.initiativetracker.commands.StartCommand
import de.lehrbaum.initiativetracker.dtos.CombatantDTO
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
	private val combatController: CombatController,
	private val delegate: Delegate
) {
	var sessionId = MutableStateFlow<Int?>(null)

	// IDEA: Return Flow of Progress/Result that allows the UI to notify the user about a successful connection etc
	// also could possibly replace the delegate
	// https://discuss.kotlinlang.org/t/best-practice-for-coroutine-that-reports-progress/14324/13
	suspend fun joinCombatAsHost(sessionId: Int): Result {
		return sharedHttpClient.buildConfigWebsocket {
			val result = joinSessionAsHost(sessionId)
			Napier.d("Result of joining session $sessionId as host: $result")
			if (result == Result.SUCCESS) {
				launch { receiveEvents() }
				shareCombatUpdates()
			}
			result
		}
	}

	private suspend fun DefaultClientWebSocketSession.joinSessionAsHost(sessionId: Int): Result {
		val startMessage = StartCommand.JoinAsHost(sessionId) as StartCommand
		this.sendSerialized(startMessage)
		val response = receiveDeserialized<StartCommand.JoinAsHost.Response>()
		return when (response) {
			is StartCommand.JoinAsHost.JoinedAsHost -> {
				val combatState = response.combatDTO
				val combatants = combatState.combatants.map(CombatantDTO::toModel)
				combatController.overwriteWithExistingCombat(combatants, combatState.activeCombatantIndex)
				Result.SUCCESS
			}
			StartCommand.JoinAsHost.SessionAlreadyHasHost -> Result.ALREADY_HOSTED
			StartCommand.JoinAsHost.SessionNotFound -> Result.NOT_FOUND
		}
	}

	suspend fun shareCombatState() {
		sharedHttpClient.buildConfigWebsocket {
			startHostingSession()
			launch { shareCombatUpdates() }
			receiveEvents()
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
				Napier.d("Sent combat update")
				sendSerialized(HostCommand.CombatUpdatedCommand(it) as HostCommand)
			}
	}

	private suspend fun DefaultClientWebSocketSession.receiveEvents() {
		while (true) {
			val incoming = receiveDeserialized<ServerToHostCommand>()
			Napier.d("Received command $incoming")
			when (incoming) {
				is ServerToHostCommand.AddCombatant -> {
					val combatant = incoming.combatant.toModel()
					delegate.addCombatant(combatant)
				}
			}
		}
	}

	enum class Result {
		SUCCESS,
		ALREADY_HOSTED,
		NOT_FOUND
	}

	interface Delegate {
		suspend fun addCombatant(combatantModel: CombatantModel)
	}
}
