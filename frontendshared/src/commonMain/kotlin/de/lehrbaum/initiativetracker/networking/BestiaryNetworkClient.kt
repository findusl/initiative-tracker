package de.lehrbaum.initiativetracker.networking

import androidx.compose.ui.graphics.ImageBitmap
import de.lehrbaum.initiativetracker.GlobalInstances
import de.lehrbaum.initiativetracker.networking.bestiary.BestiaryCollectionDTO
import de.lehrbaum.initiativetracker.networking.bestiary.HpDTO
import de.lehrbaum.initiativetracker.networking.bestiary.MonsterDTO
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.request
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.plus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.*

private const val TAG = "BestiaryNetworkClient"

interface BestiaryNetworkClient {
	val monsters: Flow<List<MonsterDTO>>

	suspend fun loadImage(monsterDTO: MonsterDTO): ImageBitmap
}

class BestiaryNetworkClientImpl(private val httpClient: HttpClient): BestiaryNetworkClient {

	private val monsterSources = flow<Map<String, String>> {
		emit(GlobalInstances.httpClient.request("https://5e.tools/data/bestiary/index.json").body())
	}

	@ExperimentalCoroutinesApi // interestingly, this is not required to propagate to the interface, no opt-in required
	override val monsters = monsterSources
		.transformLatest { spellSources ->
			spellSources.values.forEach { jsonFileName ->
				val url = "https://5e.tools/data/bestiary/$jsonFileName"
				try {
					Napier.v("Loading bestiary from $url", tag = TAG)
					val bestiary = httpClient.request(url).body<BestiaryCollectionDTO>()
					emit(bestiary.monster)
				} catch (e: Exception) {
					Napier.e("Failed to load from $url", e, TAG)
				}
			}
		}
		.runningFold<List<MonsterDTO>, PersistentList<MonsterDTO>>(persistentListOf()) { accumulated, newResource ->
			accumulated + newResource // this is surprisingly performant due to the Trie implementation of persistent list
		}
		.catch {
			Napier.e("Error loading bestiary", it, tag = TAG)
			if (it is Exception) { // for offline testing
				// TODO find some way to identify the offline exception platform independent
				emit(persistentListOf(MonsterDTO(name = "Offline Monster", dex = 14, source = "MM", hp = HpDTO(average = 42))))
			} else {
				emit(persistentListOf())
			}
		}
		.flowOn(Dispatchers.IO)

	override suspend fun loadImage(monsterDTO: MonsterDTO): ImageBitmap {
		val response = httpClient.get {

		}
		Napier.i("Got response $response", tag = TAG)
		
		TODO("Not yet implemented")
	}


}
