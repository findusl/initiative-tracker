package de.lehrbaum.initiativetracker.networking

import de.lehrbaum.initiativetracker.data.BackendUri
import de.lehrbaum.initiativetracker.data.CombatLink
import de.lehrbaum.initiativetracker.dtos.CombatModel
import de.lehrbaum.initiativetracker.dtos.CombatantModel
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

const val SESSION_PATH = "/session"

class BackendNetworkClient(private val httpClient: HttpClient) {
	suspend fun createSession(
		combatants: List<CombatantModel>,
		activeCombatantIndex: Int,
		backendUri: BackendUri
	): Result<Int> {
		val combatDTO = CombatModel(activeCombatantIndex, combatants)
		return runCatchingNested {
			withContext(Dispatchers.IO) {
				val response = httpClient.post {
					backendHttpUrl(backendUri, SESSION_PATH)
					contentType(ContentType.Application.Json)
					setBody(combatDTO)
				}
				Napier.i("Response for create Session: $response")
				response.bodyOrFailure()
			}
		}
	}

	suspend fun deleteSession(combatLink: CombatLink): Result<Unit> {
		return runCatching {
			withContext(Dispatchers.IO) {
				Napier.v("Attempting to delete Session $combatLink")
		val response = httpClient.delete {
			val path = combatLink.sessionId?.let { "$SESSION_PATH/$it" } ?: SESSION_PATH
			backendHttpUrl(combatLink.backendUri, path)
		}
		Napier.i("Response for delete Session: $response")
			}
		}
	}
}