package de.lehrbaum.initiativetracker.backend

import io.ktor.server.application.Application

expect fun Application.platformSpecificSetup()
