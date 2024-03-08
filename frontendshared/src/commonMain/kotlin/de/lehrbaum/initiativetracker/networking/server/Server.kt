package de.lehrbaum.initiativetracker.networking.server

import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.embeddedServer
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.seconds

class Server() {

	var port: Int? = null
		private set

	// Need some way to get port blocking

	private var engine: ApplicationEngine? = null

	val isRunning: Boolean
		get() = engine?.application?.isActive == true

	fun startServer() {
		engine?.stop()
		val server = embeddedServer(CIO, port = 0) {
			configureSerialization()
			//configureSockets()
			configureRouting()

		}.start()
		GlobalScope.launch {
			println("Getting Connectors")
			port = server.resolvedConnectors().first().port
			println("Got connectors $port")
		}
	}

	fun stopServer() {
		engine?.stop()
	}

	private fun Application.configureRouting() {
		routing {
			get {
				call.respond("It worked!")
			}
		}
	}

	private fun Application.configureSockets() {
		install(WebSockets) {
			contentConverter = KotlinxWebsocketSerializationConverter(Json)
			masking = false
			this.pingPeriodMillis = 9.seconds.inWholeMilliseconds
			this.timeoutMillis = 30.seconds.inWholeMilliseconds
		}
	}

	private fun Application.configureSerialization() {
		install(ContentNegotiation) {
			json(Json {
				ignoreUnknownKeys = true
			})
		}
	}
}
