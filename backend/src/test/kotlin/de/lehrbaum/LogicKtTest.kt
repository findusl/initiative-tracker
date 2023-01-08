package de.lehrbaum

import io.ktor.server.websocket.DefaultWebSocketServerSession
import io.ktor.server.websocket.receiveDeserialized
import org.mockito.kotlin.mock
import kotlin.test.Test

internal class LogicKtTest {

	@Test
	fun testStartNewSession() {
		@Suppress("UNUSED_VARIABLE") // will continue later
		val session = mock<DefaultWebSocketServerSession> {
			onBlocking { receiveDeserialized<Any>() }
		}
	}
}
