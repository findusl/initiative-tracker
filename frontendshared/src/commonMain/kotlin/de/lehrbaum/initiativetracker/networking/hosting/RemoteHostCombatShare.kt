package de.lehrbaum.initiativetracker.networking.hosting

import de.lehrbaum.initiativetracker.GlobalInstances
import de.lehrbaum.initiativetracker.bl.CombatController
import de.lehrbaum.initiativetracker.bl.HostCombatShare
import de.lehrbaum.initiativetracker.bl.HostEventHandler
import de.lehrbaum.initiativetracker.data.CombatLink
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
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "RemoteHostCombatShare"

class RemoteHostCombatShare(
	private val combatLink: CombatLink,
	private val combatController: CombatController,
) : HostCombatShare {
	private val hostEventHandler = HostEventHandler(combatController)

	override val hostConnectionState = flow {
		emit(HostConnectionState.Connecting)
		try {
			GlobalInstances.httpClient.buildBackendWebsocket(combatLink.backendUri) {
				if (joinSessionAsHost(this@flow)) {
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
	}.distinctUntilChanged()
		.flowOn(Dispatchers.IO)

	private suspend fun DefaultClientWebSocketSession.joinSessionAsHost(collector: FlowCollector<HostConnectionState>): Boolean {
		val startMessage: StartCommand =
			combatLink.sessionId?.let { StartCommand.JoinAsHostById(it.id) } ?: StartCommand.JoinDefaultSessionAsHost
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
			withContext(Dispatchers.Main) {
				val response = hostEventHandler.handleEvent(incoming)
				sendSerialized(response)
			}
		}
	}
}

sealed interface HostConnectionState {
	data object Connecting : HostConnectionState

	data object Connected : HostConnectionState

	data class Disconnected(val reason: String) : HostConnectionState
}
