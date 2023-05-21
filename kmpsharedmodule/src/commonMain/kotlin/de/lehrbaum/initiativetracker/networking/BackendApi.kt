package de.lehrbaum.initiativetracker.networking

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.random.Random

class BackendApi {
	suspend fun createSession(): Int {
		delay(10)
		return Random.nextInt(10000)
	}

	suspend fun deleteSession() = withContext(Dispatchers.IO)  {
		delay(10)
	}
}