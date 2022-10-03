package de.lehrbaum.initiativetracker.bestiary

import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.request
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.serialization.json.Json

private const val TAG = "BestiaryNetworkClient"

class BestiaryNetworkClient {
	private val httpClient = HttpClient(Android) {
		install(ContentNegotiation) {
			json(Json {
				isLenient = true
				ignoreUnknownKeys = true
			})
		}
		install(Logging) {
			logger = object : Logger {
				override fun log(message: String) {
					Napier.i(message, null, TAG)
				}
			}
			level = LogLevel.NONE // change for debugging, lot of logs
		}
	}

	private val spellSources = flow<Map<String, String>> {
		emit(httpClient.request("https://5e.tools/data/bestiary/index.json").body())
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
		}
}