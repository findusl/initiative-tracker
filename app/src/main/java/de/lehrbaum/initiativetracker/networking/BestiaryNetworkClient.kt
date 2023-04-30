package de.lehrbaum.initiativetracker.networking

import de.lehrbaum.initiativetracker.GlobalInstances
import de.lehrbaum.initiativetracker.networking.bestiary.BestiaryCollectionDTO
import io.github.aakira.napier.Napier
import io.ktor.client.call.body
import io.ktor.client.request.request
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapLatest

private const val TAG = "BestiaryNetworkClient"

class BestiaryNetworkClient {

	private val spellSources = flow<Map<String, String>> {
		emit(GlobalInstances.httpClient.request("https://5e.tools/data/bestiary/index.json").body())
	}

	@OptIn(ExperimentalCoroutinesApi::class)
	val monsters = spellSources
		.mapLatest { spellSources ->
			coroutineScope {
				val result = spellSources.values.map { jsonFileName ->
					val url = "https://5e.tools/data/bestiary/$jsonFileName"
					Napier.v("Loading bestiary from $url", tag = TAG)
					async {
						try {
							GlobalInstances.httpClient.request(url).body<BestiaryCollectionDTO>()
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
		}
}