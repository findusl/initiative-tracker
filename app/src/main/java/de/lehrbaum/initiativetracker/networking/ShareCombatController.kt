package de.lehrbaum.initiativetracker.networking

import de.lehrbaum.initiativetracker.commands.StartCommand
import de.lehrbaum.initiativetracker.logic.CombatController
import io.github.aakira.napier.Napier
import io.ktor.client.plugins.websocket.sendSerialized
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.websocket.close
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

const val BASE_REMOTE_URL = "https://de-lehrbaum-initiative-tracker.ew.r.appspot.com/"
const val BASE_LOCAL_URL = "http://10.0.2.2:8080/"
private const val TAG = "ShareCombatController"

class ShareCombatController(
	private val combatController: CombatController
) {
	private var collectionJob: Job? = null

	val sessionId = Random.nextInt(999999)

	val isSharing: Boolean
		get() = collectionJob != null

	@OptIn(FlowPreview::class)
	fun startSharing(parentScope: CoroutineScope) {
		if (collectionJob != null) return
		parentScope.launch {
			sharedHttpClient.webSocket(host = "10.0.2.2", port = 8080, path = "/session") {
				val startMessage = StartCommand.StartHosting() as StartCommand
				this.sendSerialized(startMessage)
				close()
			}
		}
		collectionJob = parentScope.launch {
			combine(combatController.combatants, combatController.activeCombatantIndex) { combatants, activeCombatantIndex ->
				CombatDTO(activeCombatantIndex, combatants.map { CombatantDTO(it) }.toList())
			}
				.debounce(500.milliseconds)
				.collectLatest {
					updateRemoteState(it)
				}
		}
	}

	private suspend fun updateRemoteState(combatDTO: CombatDTO) {
		val response = sharedHttpClient.post {
			url("${BASE_REMOTE_URL}session/$sessionId")
			contentType(ContentType.Application.Json)
			setBody(combatDTO)
		}
		Napier.i("Response for sharing $response", tag = TAG)
	}

	fun stopSharing() {
		collectionJob?.cancel()
		collectionJob = null
	}
}