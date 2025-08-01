package de.lehrbaum.initiativetracker.networking

import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.darwin.Darwin

actual fun createPlatformSpecificHttpClient(initializationBlock: HttpClientConfig<*>.() -> Unit): HttpClient =
	HttpClient(Darwin) {
		engine {
			configureSession {
				Napier.i("Configure session $this")
			}
			configureRequest {
				Napier.i("Configure request $this")
			}
		}
		initializationBlock()
	}
