package de.lehrbaum.initiativetracker

import de.lehrbaum.initiativetracker.data.GeneralSettingsRepository
import de.lehrbaum.initiativetracker.networking.BestiaryNetworkClient
import de.lehrbaum.initiativetracker.networking.BestiaryNetworkClientImpl
import de.lehrbaum.initiativetracker.networking.OpenAiNetworkClientProvider
import de.lehrbaum.initiativetracker.networking.createDefaultHttpClient

/**
 * Should replace with dependency injection in the long run.
 *
 * Using lazy to avoid tests to crash even if they don't use the instances
 */
object GlobalInstances {
    val httpClient by lazy { createDefaultHttpClient() }
	val bestiaryNetworkClient: BestiaryNetworkClient by lazy { BestiaryNetworkClientImpl(httpClient) }
	val generalSettingsRepository by lazy { GeneralSettingsRepository() }
	val openAiNetworkClientProvider by lazy { OpenAiNetworkClientProvider(generalSettingsRepository) }
}
