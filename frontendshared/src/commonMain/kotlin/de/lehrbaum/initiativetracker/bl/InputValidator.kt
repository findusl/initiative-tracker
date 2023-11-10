package de.lehrbaum.initiativetracker.bl

object InputValidator {
	fun isValidHost(input: String): Boolean {
		// Just some simple verifications. The app currently cannot handle paths, so slash is not allowed
		return !(input.isBlank() || input.contains('/') || input.contains(' '))
	}
}