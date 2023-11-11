package de.lehrbaum.initiativetracker.bl

import de.lehrbaum.initiativetracker.GlobalInstances
import de.lehrbaum.initiativetracker.bl.ClientCombatState.*
import de.lehrbaum.initiativetracker.bl.data.CombatLink
import de.lehrbaum.initiativetracker.dtos.CombatModel
import de.lehrbaum.initiativetracker.dtos.CombatantModel
import de.lehrbaum.initiativetracker.dtos.commands.ClientCommand
import de.lehrbaum.initiativetracker.dtos.commands.JoinSessionResponse
import de.lehrbaum.initiativetracker.dtos.commands.ServerToClientCommand
import de.lehrbaum.initiativetracker.dtos.commands.StartCommand
import de.lehrbaum.initiativetracker.networking.buildBackendWebsocket
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

class ClientCombatSession(val combatLink: CombatLink) {

	private val outgoingMutex = Mutex()

	private var outgoingContinuation: CancellableContinuation<Boolean>? = null

	private var webSocketSession: DefaultClientWebSocketSession? = null

	val state = flow {
		emit(Connecting)
		try {
			GlobalInstances.httpClient.buildBackendWebsocket(combatLink.backend) {
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
		val joinSessionRequest: StartCommand =
			combatLink.sessionId?.let { StartCommand.JoinSessionById(it) } ?: StartCommand.JoinDefaultSession
		sendSerialized(joinSessionRequest)
		val response = receiveDeserialized<JoinSessionResponse>()
		when (response) {
			is JoinSessionResponse.JoinedSession -> collector.emit(Connected(response.combatModel))
			is JoinSessionResponse.SessionNotFound ->
				collector.emit(Disconnected("Session for $combatLink not found"))
		}
		return response is JoinSessionResponse.JoinedSession
	}

	private suspend fun DefaultClientWebSocketSession.handleUpdates(collector: FlowCollector<ClientCombatState>) {
		while (true) {
			val message = receiveDeserialized<ServerToClientCommand>()
			// different approach with the for(message in collection) might be more readable. But requires complex iterator code
			// val message = Json.decodeFromString<ServerToClientCommand>((frame as Frame.Text).readText())
			when (message) {
				is ServerToClientCommand.CombatUpdatedCommand -> collector.emit(Connected(message.combat))
				is ServerToClientCommand.CombatEnded -> {
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
		return sendClientCommand(ClientCommand.AddCombatant(combatantModel))
	}

	suspend fun requestEditCharacter(combatantModel: CombatantModel): Boolean {
		return sendClientCommand(ClientCommand.EditCombatant(combatantModel))
	}

	suspend fun requestDamageCharacter(combatantId: Long, damage: Int, ownerId: Long): Boolean {
		return sendClientCommand(ClientCommand.DamageCombatant(combatantId, damage, ownerId))
	}

	@OptIn(DelicateCoroutinesApi::class)
	private suspend fun sendClientCommand(command: ClientCommand): Boolean {
		Napier.d("Attempting to send client command $command. Mutex locked: ${outgoingMutex.isLocked}")
		outgoingMutex.withLock { // only one command at a time
			val webSocketSession = this.webSocketSession ?: return false
			webSocketSession.sendSerialized(command)
			return suspendCancellableCoroutine {
				outgoingContinuation = it
				it.invokeOnCancellation {
					// Using GlobalScope as the cancellation of a command cannot be bound to any Scope
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
		constructor(combatModel: CombatModel):
			this(combatModel.activeCombatantIndex, combatModel.combatants)
	}
	data class Disconnected(val reason: String): ClientCombatState
}
