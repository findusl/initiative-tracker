package de.lehrbaum.initiativetracker.bl

import de.lehrbaum.initiativetracker.GlobalInstances
import de.lehrbaum.initiativetracker.bl.data.CombatLink
import de.lehrbaum.initiativetracker.dtos.CombatModel
import de.lehrbaum.initiativetracker.dtos.commands.HostCommand
import de.lehrbaum.initiativetracker.dtos.commands.ServerToHostCommand
import de.lehrbaum.initiativetracker.dtos.commands.StartCommand
import de.lehrbaum.initiativetracker.dtos.commands.StartCommand.HostingCommand
import de.lehrbaum.initiativetracker.networking.buildBackendWebsocket
import io.github.aakira.napier.Napier
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.receiveDeserialized
import io.ktor.client.plugins.websocket.sendSerialized
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.flow.*
import kotlin.time.Duration.Companion.milliseconds

private const val TAG = "HostCombatSession"

class HostCombatSession(
	val combatLink: CombatLink,
	private val combatController: CombatController,
) {
	val hostConnectionState = flow {
		emit(HostConnectionState.Connecting)
		try {
			GlobalInstances.httpClient.buildBackendWebsocket(combatLink.backend) {
				if(joinSessionAsHost(this@flow)) {
					launch { shareCombatUpdates() }
					receiveEvents()
					emit(HostConnectionState.Disconnected("Event receiving ended normally"))
				}
			}
		} catch (e: Exception) {
			if (e is ClosedReceiveChannelException) {
				Napier.i("Channel was closed.", tag = TAG)
			} else {
				Napier.w("Exception in Remote combat: ${e.message}", e, tag = TAG)
			}
			emit(HostConnectionState.Disconnected("Exception $e"))
		}
	}
		.distinctUntilChanged()
		.flowOn(Dispatchers.IO)

	private suspend fun DefaultClientWebSocketSession.joinSessionAsHost(collector: FlowCollector<HostConnectionState>): Boolean {
		val startMessage: StartCommand =
			combatLink.sessionId?.let { StartCommand.JoinAsHostById(it) } ?: StartCommand.JoinDefaultSessionAsHost
		this.sendSerialized(startMessage)
		val response = receiveDeserialized<HostingCommand.Response>()
		when (response) {
			is HostingCommand.JoinedAsHost -> {
				val combatState = response.combatModel
				val combatants = combatState.combatants
				combatController.overwriteWithExistingCombat(combatants, combatState.activeCombatantIndex)
				collector.emit(HostConnectionState.Connected)
			}
			HostingCommand.SessionAlreadyHasHost -> {
				collector.emit(HostConnectionState.Disconnected("Session $combatLink already has a host."))
			}
			HostingCommand.SessionNotFound -> {
				collector.emit(HostConnectionState.Disconnected("Session $combatLink not found."))
			}
		}
		return response is HostingCommand.JoinedAsHost
	}

	@OptIn(FlowPreview::class)
	private suspend fun DefaultClientWebSocketSession.shareCombatUpdates() {
		combine(combatController.activeCombatantIndex, combatController.combatants, ::CombatModel)
			.debounce(50.milliseconds)
			.distinctUntilChanged()
			.collectLatest {
				Napier.v("Sent combat update for Session $combatLink")
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
						combatController.handleDamageCombatantRequest(incoming.targetId, incoming.damage, incoming.ownerId)
					}
					sendSerialized(HostCommand.CommandCompleted(result) as HostCommand)
				}
			}
		}
	}
}

sealed interface HostConnectionState {
	data object Connecting: HostConnectionState
	data object Connected: HostConnectionState
	data class Disconnected(val reason: String): HostConnectionState
}