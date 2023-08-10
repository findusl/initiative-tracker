package de.lehrbaum.initiativetracker.networking

import de.lehrbaum.initiativetracker.GlobalInstances
import de.lehrbaum.initiativetracker.networking.bestiary.BestiaryCollectionDTO
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.request
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest

private const val TAG = "BestiaryNetworkClient"

@Suppress("OPT_IN_USAGE")
class BestiaryNetworkClient(httpClient: HttpClient) {

	private val monsterSources = flow<Map<String, String>> {
		emit(GlobalInstances.httpClient.request("https://5e.tools/data/bestiary/index.json").body())
	}

	val monsters = monsterSources
		.mapLatest { spellSources ->
			coroutineScope {
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
			emit(emptyList())
		}
		.flowOn(Dispatchers.IO)

	// To be implemented, moved from old app
	/*
	private val bestiaryNetworkClient = BestiaryNetworkClient()

	val allMonsterNamesLiveData = bestiaryNetworkClient.monsters
		.map { monsters -> monsters.map { it.name }.toTypedArray() }
		.flowOn(Dispatchers.IO)
		// immediately start fetching as it takes a while
		.stateIn(viewModelScope, SharingStarted.Eagerly, arrayOf())
		.asLiveData()
	 */
}