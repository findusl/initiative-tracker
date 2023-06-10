package de.lehrbaum.initiativetracker.bl

import de.lehrbaum.initiativetracker.GlobalInstances
import de.lehrbaum.initiativetracker.bl.ClientCombatState.*
import de.lehrbaum.initiativetracker.bl.model.CombatantModel
import de.lehrbaum.initiativetracker.commands.ClientCommand
import de.lehrbaum.initiativetracker.commands.JoinSessionResponse
import de.lehrbaum.initiativetracker.commands.ServerToClientCommand
import de.lehrbaum.initiativetracker.commands.StartCommand
import de.lehrbaum.initiativetracker.dtos.CombatDTO
import de.lehrbaum.initiativetracker.dtos.CombatantDTO
import de.lehrbaum.initiativetracker.networking.buildConfigWebsocket
import io.github.aakira.napier.Napier
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.receiveDeserialized
import io.ktor.client.plugins.websocket.sendSerialized
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.resume

private const val TAG = "ClientCombatSession"

class ClientCombatSession(val sessionId: Int) {

	private val outgoingMutex = Mutex()

	private var outgoingContinuation: CancellableContinuation<Boolean>? = null

	private var webSocketSession: DefaultClientWebSocketSession? = null

	val state = flow {
		emit(Connecting)
		try {
			GlobalInstances.httpClient.buildConfigWebsocket {
				webSocketSession = this
				if (joinSessionAsClient(this@flow)) {
					handleUpdates(this@flow)
				}
			}
		} catch (e: Exception) {
			Napier.i("Exception in Remote combat: ${e.message}", tag = TAG)
			emit(Disconnected("Exception $e"))
		}
	}
		.distinctUntilChanged()
		.flowOn(Dispatchers.IO)

	private suspend fun DefaultClientWebSocketSession.joinSessionAsClient(collector: FlowCollector<ClientCombatState>): Boolean {
		val joinSessionRequest = StartCommand.JoinSession(sessionId) as StartCommand
		sendSerialized(joinSessionRequest)
		val response = receiveDeserialized<JoinSessionResponse>()
		when (response) {
			is JoinSessionResponse.JoinedSession -> collector.emit(Connected(response.combatDTO))
			JoinSessionResponse.SessionNotFound ->
				collector.emit(Disconnected("Session with id $sessionId not found"))
		}
		return response is JoinSessionResponse.JoinedSession
	}

	private suspend fun DefaultClientWebSocketSession.handleUpdates(collector: FlowCollector<ClientCombatState>) {
		while (true) {
			val message = receiveDeserialized<ServerToClientCommand>()
			when (message) {
				is ServerToClientCommand.CombatUpdatedCommand -> collector.emit(Connected(message.combat))
				ServerToClientCommand.CombatEnded -> {
					collector.emit(Disconnected("Combat ended"))
					return
				}
				is ServerToClientCommand.CommandCompleted -> {
					if (outgoingContinuation?.isActive == true) {
						outgoingContinuation?.resume(message.accepted)
						outgoingContinuation = null // ensure we do not resume it again
					}
				}
			}
		}
	}

	suspend fun requestAddCharacter(combatantModel: CombatantModel): Boolean {
		return sendClientCommand(ClientCommand.AddCombatant(combatantModel.toDTO()))
	}

	suspend fun requestEditCharacter(combatantModel: CombatantModel): Boolean {
		return sendClientCommand(ClientCommand.EditCombatant(combatantModel.toDTO()))
	}

	@Suppress("OPT_IN_IS_NOT_ENABLED") // The cancellation of a command cannot be bound to any Scope
	@OptIn(DelicateCoroutinesApi::class)
	private suspend fun sendClientCommand(command: ClientCommand): Boolean {
		Napier.d("Attempting to send client command $command. Mutex locked: ${outgoingMutex.isLocked}")
		outgoingMutex.withLock { // only one command at a time
			val webSocketSession = this.webSocketSession ?: return false
			webSocketSession.sendSerialized(command)
			return suspendCancellableCoroutine {
				outgoingContinuation = it
				it.invokeOnCancellation {
					GlobalScope.launch {
						webSocketSession.sendSerialized(ClientCommand.CancelCommand as ClientCommand)
					}
				}
			}
		}
	}
}

sealed interface ClientCombatState {
	object Connecting: ClientCombatState
	data class Connected(val activeCombatantIndex: Int, val combatants: List<CombatantModel>): ClientCombatState {
		constructor(combatDTO: CombatDTO):
			this(combatDTO.activeCombatantIndex, combatDTO.combatants.map(CombatantDTO::toModel))
	}
	data class Disconnected(val reason: String): ClientCombatState
}
