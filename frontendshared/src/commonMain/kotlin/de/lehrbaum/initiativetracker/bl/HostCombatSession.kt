package de.lehrbaum.initiativetracker.bl

import de.lehrbaum.initiativetracker.GlobalInstances
import de.lehrbaum.initiativetracker.dtos.CombatModel
import de.lehrbaum.initiativetracker.dtos.commands.HostCommand
import de.lehrbaum.initiativetracker.dtos.commands.ServerToHostCommand
import de.lehrbaum.initiativetracker.dtos.commands.StartCommand
import de.lehrbaum.initiativetracker.networking.buildBackendWebsocket
import io.github.aakira.napier.Napier
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.receiveDeserialized
import io.ktor.client.plugins.websocket.sendSerialized
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.time.Duration.Companion.milliseconds

private const val TAG = "HostCombatSession"

class HostCombatSession(
	val sessionId: Int,
	private val combatController: CombatController,
	private val delegate: Delegate
) {
	val hostConnectionState = flow {
		emit(HostConnectionState.Connecting)
		try {
			GlobalInstances.httpClient.buildBackendWebsocket {
				if(joinSessionAsHost(this@flow)) {
					launch { shareCombatUpdates() }
					receiveEvents()
					emit(HostConnectionState.Disconnected("Event receiving ended normally"))
				}
			}
		} catch (e: Exception) {
			Napier.i("Exception in Remote combat: ${e.message}", tag = TAG)
			emit(HostConnectionState.Disconnected("Exception $e"))
		}
	}
		.distinctUntilChanged()
		.flowOn(Dispatchers.IO)

	private suspend fun DefaultClientWebSocketSession.joinSessionAsHost(collector: FlowCollector<HostConnectionState>): Boolean {
		val startMessage = StartCommand.JoinAsHost(sessionId) as StartCommand
		this.sendSerialized(startMessage)
		val response = receiveDeserialized<StartCommand.JoinAsHost.Response>()
		when (response) {
			is StartCommand.JoinAsHost.JoinedAsHost -> {
				val combatState = response.combatModel
				val combatants = combatState.combatants
				combatController.overwriteWithExistingCombat(combatants, combatState.activeCombatantIndex)
				collector.emit(HostConnectionState.Connected)
			}
			StartCommand.JoinAsHost.SessionAlreadyHasHost -> {
				collector.emit(HostConnectionState.Disconnected("Session $sessionId already has a host."))
			}
			StartCommand.JoinAsHost.SessionNotFound -> {
				collector.emit(HostConnectionState.Disconnected("Session $sessionId not found."))
			}
		}
		return response is StartCommand.JoinAsHost.JoinedAsHost
	}

	@Suppress("OPT_IN_IS_NOT_ENABLED")
	@OptIn(FlowPreview::class)
	private suspend fun DefaultClientWebSocketSession.shareCombatUpdates() {
		combine(combatController.activeCombatantIndex, combatController.combatants, ::CombatModel)
			.debounce(200.milliseconds)
			.distinctUntilChanged()
			.collectLatest {
				Napier.d("Sent combat update for Session $sessionId")
				sendSerialized(HostCommand.CombatUpdatedCommand(it) as HostCommand)
			}
	}

	private suspend fun DefaultClientWebSocketSession.receiveEvents() {
		while (true) {
			val incoming = receiveDeserialized<ServerToHostCommand>()
			Napier.d("Received command $incoming")
			when (incoming) {
				is ServerToHostCommand.AddCombatant -> {
					withContext(Dispatchers.Main) {
						combatController.addCombatant(incoming.combatant)
					}
					sendSerialized(HostCommand.CommandCompleted(true) as HostCommand)
				}

				is ServerToHostCommand.EditCombatant -> {
					withContext(Dispatchers.Main) {
						combatController.updateCombatant(incoming.combatant)
					}
					sendSerialized(HostCommand.CommandCompleted(true) as HostCommand)
				}

				is ServerToHostCommand.DamageCombatant -> {
					val result = withContext(Dispatchers.Main) {
						delegate.handleDamageCombatantCommand(incoming)
					}
					sendSerialized(HostCommand.CommandCompleted(result) as HostCommand)
				}
			}
		}
	}

	interface Delegate {
		suspend fun handleDamageCombatantCommand(command: ServerToHostCommand.DamageCombatant): Boolean
	}
}

sealed interface HostConnectionState {
	object Connecting: HostConnectionState
	object Connected: HostConnectionState
	data class Disconnected(val reason: String): HostConnectionState
}