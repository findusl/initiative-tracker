package de.lehrbaum.initiativetracker.networking.hosting

import io.github.aakira.napier.Napier
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.seconds

private val TAG = LocalHostServer::class.simpleName

class LocalHostServer(private val localHostCombatShare: LocalHostCombatShare) {

	init {
	    startServer()
	}

	val port = MutableStateFlow<Int?>(null)

	// Need some way to get port blocking

	private var server =
		embeddedServer(CIO, port = 0) {
			configureSerialization()
			configureSockets()
			configureRouting()
		}
	private var engineScope = server.application

	private val isRunning: Boolean
		get() = engineScope.isActive

	private fun startServer() {
		if (isRunning) return
		server.start()
		Napier.i("Started LocalHostServer", tag = TAG)
		server.application.launch {
			port.value = server.engine.resolvedConnectors().first().port
			Napier.i("Got $port", tag = TAG)
		}
	}

	fun stopServer() {
		Napier.i("Stop LocalHostServer", tag = TAG)
		server.stop()
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
