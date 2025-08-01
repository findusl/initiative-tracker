package de.lehrbaum.initiativetracker.networking.hosting

import io.github.aakira.napier.Napier
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.embeddedServer
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

private val TAG = LocalHostServer::class.simpleName

class LocalHostServer {
	var port: Int? = null
		private set

	// Need some way to get port blocking

	private var engine: ApplicationEngine? = null
	private var engineScope: CoroutineScope? = null

	val isRunning: Boolean
		get() = engineScope?.isActive == true

	fun hostCombat(localHostCombatShare: LocalHostCombatShare) {
	}

	fun startServer() {
		engine?.stop()
		engine = embeddedServer(CIO, port = 0) {
			configureSerialization()
			configureSockets()
			configureRouting()
		}.also {
			it.start()
			Napier.i("Started LocalHostServer", tag = TAG)
			it.application.launch {
				port = it.engine.resolvedConnectors().first().port
				Napier.i("Got $port", tag = TAG)
			}
			engineScope = it.application
		}.engine
	}

	fun stopServer() {
		Napier.i("Stop LocalHostServer", tag = TAG)
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
			json(
				Json {
					ignoreUnknownKeys = true
				},
			)
		}
	}
}
