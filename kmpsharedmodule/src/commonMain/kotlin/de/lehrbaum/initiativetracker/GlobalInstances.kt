package de.lehrbaum.initiativetracker

import de.lehrbaum.initiativetracker.networking.createDefaultHttpClient

object GlobalInstances {
    val httpClient = createDefaultHttpClient()
}
