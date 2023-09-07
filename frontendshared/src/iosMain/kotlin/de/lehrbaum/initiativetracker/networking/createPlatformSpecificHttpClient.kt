package de.lehrbaum.initiativetracker.networking

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.darwin.Darwin

actual fun createPlatformSpecificHttpClient(initializationBlock: HttpClientConfig<*>.() -> Unit): HttpClient =
	HttpClient(Darwin) {
		initializationBlock()
	}
