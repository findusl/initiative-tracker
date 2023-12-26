package de.lehrbaum.initiativetracker

import de.lehrbaum.initiativetracker.bl.data.GeneralSettingsRepository
import de.lehrbaum.initiativetracker.networking.BackendNetworkClient
import de.lehrbaum.initiativetracker.networking.BestiaryNetworkClient
import de.lehrbaum.initiativetracker.networking.BestiaryNetworkClientImpl
import de.lehrbaum.initiativetracker.networking.OpenAiNetworkClient
import de.lehrbaum.initiativetracker.networking.createDefaultHttpClient

/**
 * Should replace with dependency injection in the long run.
 *
 * Using lazy to avoid tests to crash even if they don't use the instances
 */
object GlobalInstances {
    val httpClient by lazy { createDefaultHttpClient() }
	val backendNetworkClient by lazy { BackendNetworkClient(httpClient) }
	val bestiaryNetworkClient: BestiaryNetworkClient by lazy { BestiaryNetworkClientImpl(httpClient) }
	val openAiNetworkClient by lazy { BuildKonfig.openaiApiKey?.let { OpenAiNetworkClient(it) } }
	val generalSettingsRepository by lazy { GeneralSettingsRepository() }
}
