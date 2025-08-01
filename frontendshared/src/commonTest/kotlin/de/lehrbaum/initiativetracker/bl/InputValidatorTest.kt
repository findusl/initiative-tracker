package de.lehrbaum.initiativetracker.bl

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class InputValidatorTest {
	@Test
	fun testIsValidHostGoodCase() {
		assertTrue(InputValidator.isValidHost("localhost"))
		assertTrue(InputValidator.isValidHost("localhost:8080"))
		assertTrue(InputValidator.isValidHost("192.188.100.1"))
		assertTrue(InputValidator.isValidHost("192.188.100.1:8080"))
		assertTrue(InputValidator.isValidHost("domain.com"))
		assertTrue(InputValidator.isValidHost("sub.domain.com"))
		assertTrue(InputValidator.isValidHost("sub.domain.com:8080"))
	}

	@Test
	fun testIsValidHostShouldNotAcceptSchema() {
		assertFalse(InputValidator.isValidHost("http://localhost"))
		assertFalse(InputValidator.isValidHost("http://localhost:8080"))
		assertFalse(InputValidator.isValidHost("http://192.188.100.1"))
		assertFalse(InputValidator.isValidHost("http://192.188.100.1:8080"))
		assertFalse(InputValidator.isValidHost("http://domain.com"))
		assertFalse(InputValidator.isValidHost("http://sub.domain.com"))
		assertFalse(InputValidator.isValidHost("http://sub.domain.com:8080"))
	}

	@Test
	fun testIsValidHostShouldNotAcceptPath() {
		assertFalse(InputValidator.isValidHost("sub.domain.com/path"))
		assertFalse(InputValidator.isValidHost("sub.domain.com:8080/path"))
	}

	@Test
	fun testIsValidHostShouldNotAcceptInvalidCharacters() {
		assertFalse(InputValidator.isValidHost("local host"))
	}

	@Test
	fun testIsValidOpenAiApiKeyGoodCase() {
		assertTrue(InputValidator.isValidOpenAiApiKey("sk-abcdefghij"))
		assertTrue(InputValidator.isValidOpenAiApiKey("sk-ABCDEFGHIJ"))
		assertTrue(InputValidator.isValidOpenAiApiKey("sk-1234567890"))
		assertTrue(InputValidator.isValidOpenAiApiKey("sk-abcABC123"))
		assertTrue(InputValidator.isValidOpenAiApiKey("sk-abcdefghijklmnopqrstuvwxyz"))
	}

	@Test
	fun testIsValidOpenAiApiKeyShouldNotAcceptEmptyOrBlank() {
		assertFalse(InputValidator.isValidOpenAiApiKey(""))
		assertFalse(InputValidator.isValidOpenAiApiKey(" "))
		assertFalse(InputValidator.isValidOpenAiApiKey("\t"))
		assertFalse(InputValidator.isValidOpenAiApiKey("\n"))
	}

	@Test
	fun testIsValidOpenAiApiKeyShouldNotAcceptWrongPrefix() {
		assertFalse(InputValidator.isValidOpenAiApiKey("s-abcdefghij"))
		assertFalse(InputValidator.isValidOpenAiApiKey("SK-abcdefghij"))
		assertFalse(InputValidator.isValidOpenAiApiKey("abc-abcdefghij"))
		assertFalse(InputValidator.isValidOpenAiApiKey("abcdefghij"))
	}

	@Test
	fun testIsValidOpenAiApiKeyShouldNotAcceptTooShort() {
		assertFalse(InputValidator.isValidOpenAiApiKey("sk-abc"))
		assertFalse(InputValidator.isValidOpenAiApiKey("sk-123456"))
		assertFalse(InputValidator.isValidOpenAiApiKey("sk-abcdef"))
	}

	@Test
	fun testIsValidOpenAiApiKeyShouldNotAcceptInvalidCharacters() {
		assertFalse(InputValidator.isValidOpenAiApiKey("sk-abc def"))
		assertFalse(InputValidator.isValidOpenAiApiKey("sk-abc/def"))
		assertFalse(InputValidator.isValidOpenAiApiKey("sk-abc.def"))
		assertFalse(InputValidator.isValidOpenAiApiKey("sk-abc@def"))
	}

	@Test
	fun testIsValidOpenAiApiKeyShouldAcceptDashesAndUnderscores() {
		assertTrue(InputValidator.isValidOpenAiApiKey("sk-abc-def-ghi"))
		assertTrue(InputValidator.isValidOpenAiApiKey("sk-abc_def_ghi"))
		assertTrue(InputValidator.isValidOpenAiApiKey("sk-abc-def_ghi"))
		assertTrue(InputValidator.isValidOpenAiApiKey("sk-abc_def-ghi"))
	}
}
