package de.lehrbaum.initiativetracker.networking

import de.lehrbaum.initiativetracker.GlobalInstances
import de.lehrbaum.initiativetracker.data.GeneralSettingsRepository
import de.lehrbaum.initiativetracker.networking.bestiary.BestiaryCollectionDTO
import de.lehrbaum.initiativetracker.networking.bestiary.HpDTO
import de.lehrbaum.initiativetracker.networking.bestiary.MonsterDTO
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.request
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.plus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

private const val TAG = "BestiaryNetworkClient"

interface BestiaryNetworkClient {
	val monsters: Flow<List<MonsterDTO>>
}

class BestiaryNetworkClientImpl(
	defaultClient: HttpClient,
	settingsRepository: GeneralSettingsRepository = GlobalInstances.generalSettingsRepository,
) : BestiaryNetworkClient {
	private val httpClient = defaultClient.config {
		install(ContentNegotiation) {
			val jsonSerializer = Json {
				isLenient = true
				ignoreUnknownKeys = true
			}
			json(jsonSerializer, ContentType.Text.Plain)
		}
	}

	private val monsterSources = settingsRepository.homebrewLinksFlow.map { homebrewLinks ->
		val sources =
			httpClient
				.request("https://5e.tools/data/bestiary/index.json")
				.body<Map<String, String>>()
				.mapValues { (_, fileName) -> "https://5e.tools/data/bestiary/$fileName" }

		val homebrewSources = homebrewLinks
			.mapIndexed { index, url -> index to url }
			.associate { (index, url) -> "CustomHB$index" to url }

		sources + homebrewSources
	}

	@ExperimentalCoroutinesApi // this is not required to propagate to the interface, weird
	override val monsters = monsterSources
		.mapLatest { sources ->
			channelFlow {
				sources.values.sortSourcesByMyPreference().forEach { url ->
					launch {
						try {
							Napier.v("Loading bestiary from $url", tag = TAG)
							val bestiary = httpClient.request(url).body<BestiaryCollectionDTO>()
							send(bestiary.monster)
						} catch (e: Exception) {
							Napier.e("Failed to load from $url", e, TAG)
						}
					}
				}
			}.scan(persistentListOf<MonsterDTO>()) { accumulated, newResource ->
				accumulated + newResource // this is surprisingly performant due to the Trie implementation of persistent list
			}
		}.flattenConcat()
		.catch {
			Napier.e("Error loading bestiary", it, tag = TAG)
			if (it is Exception) { // for offline testing
				// I could not find a way to identify the offline exception platform independent
				emit(persistentListOf(MonsterDTO(name = "Offline Monster", dex = 14, source = "MM", hp = HpDTO(average = 42))))
			} else {
				emit(persistentListOf())
			}
		}.conflate()
		.flowOn(Dispatchers.IO)

	private fun Collection<String>.sortSourcesByMyPreference(): Collection<String> = shuffled()
}
