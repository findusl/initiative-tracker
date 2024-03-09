package de.lehrbaum.initiativetracker.bl

import androidx.compose.runtime.Stable
import de.lehrbaum.initiativetracker.GlobalInstances
import de.lehrbaum.initiativetracker.bl.ClientCombatState.Connected
import de.lehrbaum.initiativetracker.bl.ClientCombatState.Connecting
import de.lehrbaum.initiativetracker.bl.ClientCombatState.Disconnected
import de.lehrbaum.initiativetracker.data.CombatLink
import de.lehrbaum.initiativetracker.dtos.CombatModel
import de.lehrbaum.initiativetracker.dtos.CombatantId
import de.lehrbaum.initiativetracker.dtos.CombatantModel
import de.lehrbaum.initiativetracker.dtos.UserId
import de.lehrbaum.initiativetracker.dtos.commands.ClientCommand
import de.lehrbaum.initiativetracker.dtos.commands.JoinSessionResponse
import de.lehrbaum.initiativetracker.dtos.commands.ServerToClientCommand
import de.lehrbaum.initiativetracker.dtos.commands.StartCommand
import de.lehrbaum.initiativetracker.networking.buildBackendWebsocket
import io.github.aakira.napier.Napier
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.receiveDeserialized
import io.ktor.client.plugins.websocket.sendSerialized
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.resume

private const val TAG = "ClientCombatSession"

class ClientCombatSession(private val combatLink: CombatLink) {

	private val outgoingMutex = Mutex()

	private var outgoingContinuation: CancellableContinuation<Boolean>? = null

	private var webSocketSession: DefaultClientWebSocketSession? = null

	val state = flow {
		emit(Connecting)
		try {
			GlobalInstances.httpClient.buildBackendWebsocket(combatLink.backendUri) {
				webSocketSession = this
				if (joinSessionAsClient(this@flow)) {
					handleUpdates(this@flow)
				}
			}
		} catch (e: Exception) {
			if (e is ClosedReceiveChannelException) {
				Napier.i("Channel was closed.", tag = TAG)
			} else {
				Napier.w("Exception in Remote combat: ${e.message}", e, tag = TAG)
			}
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

	suspend fun requestDamageCharacter(targetId: CombatantId, damage: Int, ownerId: UserId): Boolean {
		return sendClientCommand(ClientCommand.DamageCombatant(targetId, damage, ownerId))
	}

	suspend fun requestFinishTurn(activeCombatantIndex: Int): Boolean {
		return sendClientCommand(ClientCommand.FinishTurn(activeCombatantIndex))
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
	data object Connecting: ClientCombatState
	@Stable
	data class Connected(val activeCombatantIndex: Int, val combatants: ImmutableList<CombatantModel>): ClientCombatState {
		constructor(combatModel: CombatModel):
			this(combatModel.activeCombatantIndex, combatModel.combatants.toImmutableList())
	}
	data class Disconnected(val reason: String): ClientCombatState
}
