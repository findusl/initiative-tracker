package de.lehrbaum.initiativetracker.networking

import io.github.aakira.napier.Napier
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

private const val TAG = "RemoteCombatController"

class RemoteCombatController(private val sessionId: Int) {

	val remoteCombat = flow<CombatDTO> {
		while (true) {
			val response = sharedHttpClient.get(BASE_REMOTE_URL + "session/" + sessionId)
			Napier.i("Remote response: $response", tag = TAG)
			emit(response.body())
			delay(1000)
		}
	}
		.flowOn(Dispatchers.IO)
}