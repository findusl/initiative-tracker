package de.lehrbaum.initiativetracker.networking

import de.lehrbaum.initiativetracker.GlobalInstances
import de.lehrbaum.initiativetracker.networking.bestiary.BestiaryCollectionDTO
import de.lehrbaum.initiativetracker.networking.bestiary.HpDTO
import de.lehrbaum.initiativetracker.networking.bestiary.MonsterDTO
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.request
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.net.UnknownHostException

private const val TAG = "BestiaryNetworkClient"

interface BestiaryNetworkClient {
	val monsters: Flow<List<MonsterDTO>>
}

class BestiaryNetworkClientImpl(httpClient: HttpClient): BestiaryNetworkClient {

	private val monsterSources = flow<Map<String, String>> {
		emit(GlobalInstances.httpClient.request("https://5e.tools/data/bestiary/index.json").body())
	}

	@ExperimentalCoroutinesApi // interestingly, this is not required to propagate to the interface, no opt-in required
	override val monsters = monsterSources
		.mapLatest { spellSources ->
			supervisorScope {
				val result = spellSources.values.map { jsonFileName ->
					val url = "https://5e.tools/data/bestiary/$jsonFileName"
					Napier.v("Loading bestiary from $url", tag = TAG)
					async {
						try {
							httpClient.request(url).body<BestiaryCollectionDTO>()
						} catch (e: Exception) {
							Napier.e("Failed to load from $url", e, TAG)
							null
						}
					}
				}.awaitAll()
					.filterNotNull()
					.flatMap(BestiaryCollectionDTO::monster)
				Napier.i("Loaded ${result.size} monsters ", tag = TAG)
				result
			}
		}
		.catch {
			Napier.e("Error loading bestiary", it, tag = TAG)
			if (it is UnknownHostException) {
				emit(listOf(MonsterDTO(name = "Offline Monster", dex = 14, source = "MM", hp = HpDTO(average = 42))))
			} else {
				emit(emptyList())
			}
		}
		.flowOn(Dispatchers.IO)
}
