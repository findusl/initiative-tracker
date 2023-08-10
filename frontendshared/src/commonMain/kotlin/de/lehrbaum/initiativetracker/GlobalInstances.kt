package de.lehrbaum.initiativetracker

import de.lehrbaum.initiativetracker.networking.*

/**
 * Not so happy yet with multiplatform dependency injection. It's also a bit overkill.
 */
object GlobalInstances {
    val httpClient = createDefaultHttpClient()
	val backendNetworkClient = BackendNetworkClient(httpClient)
	val bestiaryNetworkClient: BestiaryNetworkClient = BestiaryNetworkClientImpl(httpClient)
	val openAiNetworkClient = BuildKonfig.openaiApiKey?.let { OpenAiNetworkClient(it) }
}
