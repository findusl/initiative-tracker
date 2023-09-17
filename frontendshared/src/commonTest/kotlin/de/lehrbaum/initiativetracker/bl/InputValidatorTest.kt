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
		assertTrue(InputValidator.isValidHost("sub.domain.com/path"))
		assertTrue(InputValidator.isValidHost("sub.domain.com:8080/path"))
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
		assertFalse(InputValidator.isValidHost("http://sub.domain.com/path"))
		assertFalse(InputValidator.isValidHost("http://sub.domain.com:8080/path"))
    }

    @Test
    fun testIsValidHostShouldNotAcceptInvalidCharacters() {
		assertFalse(InputValidator.isValidHost("local:host"))
    }
}