package de.lehrbaum.initiativetracker.networking

import de.lehrbaum.initiativetracker.bl.toCombatDTO
import de.lehrbaum.initiativetracker.dtos.CombatantModel
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class BackendNetworkClient(private val httpClient: HttpClient) {
	suspend fun createSession(combatants: List<CombatantModel>, activeCombatantIndex: Int): Int = withContext(Dispatchers.IO) {
		val combatDTO = toCombatDTO(combatants, activeCombatantIndex)
		val response = httpClient.post {
			backendHttpUrl(SESSION_PATH)
			contentType(ContentType.Application.Json)
			setBody(combatDTO)
		}
		Napier.i("Response for create Session: $response")
		response.body()
	}

	suspend fun deleteSession(sessionId: Int) = withContext(Dispatchers.IO)  {
		val response = httpClient.delete {
			backendHttpUrl("$SESSION_PATH/$sessionId")
		}
		Napier.i("Response for delete Session $sessionId: $response")
	}
}