package de.lehrbaum

import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {

	@Test
	fun simpleTest() = testApplication {
		application {
            main()
        }
		val testContent = "Test Content"

		client.post("/session/1234") {
			setBody(testContent)
		}.apply {
			assertEquals(HttpStatusCode.Created, status)
		}
	}
}