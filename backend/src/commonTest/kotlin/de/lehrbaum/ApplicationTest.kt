package de.lehrbaum

import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {

	@Test
	fun simpleTest() = testApplication {
		application {
			configureSerialization()
			configureSockets()
			configureRouting()
			platformSpecificSetup()
		}
		val testContent = """{"combatants":[], "activeCombatantIndex":0 } """

		client.post("/session") {
			contentType(ContentType.Application.Json)
			setBody(testContent)
		}.apply {
			assertEquals(HttpStatusCode.Created, status)
		}
	}
}