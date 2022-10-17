package de.lehrbaum.initiativetracker.networking

import de.lehrbaum.initiativetracker.logic.CombatController
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

private const val REMOTE_URL = "https://de-lehrbaum-initiative-tracker.ew.r.appspot.com/session/"

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
		sharedHttpClient.post {
			url(REMOTE_URL + sessionId)
			contentType(ContentType.Application.Json)
			setBody(combatDTO)
		}
	}

	fun stopSharing() {
		collectionJob?.cancel()
		collectionJob = null
	}
}