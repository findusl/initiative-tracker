package de.lehrbaum.initiativetracker.networking

import de.lehrbaum.initiativetracker.bl.InputValidator
import de.lehrbaum.initiativetracker.data.GeneralSettingsRepository

class OpenAiNetworkClientProvider(private val repository: GeneralSettingsRepository) {
	private var client: OpenAiNetworkClient? = null
	private var currentApiKey: String? = null

	fun getClient(): OpenAiNetworkClient? {
		val apiKey = repository.openAiApiKey

		if (apiKey == null) {
			client = null
			currentApiKey = null
			return null
		}

		if (apiKey != currentApiKey) {
			client = OpenAiNetworkClient(apiKey)
			currentApiKey = apiKey
		}

		return client
	}

	fun isAvailable(): Boolean {
		val apiKey = repository.openAiApiKey
		return apiKey != null && InputValidator.isValidOpenAiApiKey(apiKey)
	}
}
