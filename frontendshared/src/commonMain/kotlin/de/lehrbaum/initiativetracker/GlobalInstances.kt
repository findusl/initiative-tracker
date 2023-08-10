package de.lehrbaum.initiativetracker

import de.lehrbaum.initiativetracker.networking.BackendNetworkClient
import de.lehrbaum.initiativetracker.networking.OpenAiNetworkClient
import de.lehrbaum.initiativetracker.networking.createDefaultHttpClient

/**
 * Not so happy yet with multiplatform dependency injection. It's also a bit overkill.
 */
object GlobalInstances {
    val httpClient = createDefaultHttpClient()
	val backendNetworkClient = BackendNetworkClient(httpClient)
	val openAiNetworkClient = BuildKonfig.openaiApiKey?.let { OpenAiNetworkClient(it) }
}
