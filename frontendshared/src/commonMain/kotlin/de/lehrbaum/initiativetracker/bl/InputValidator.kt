package de.lehrbaum.initiativetracker.bl

object InputValidator {
	fun isValidHost(input: String): Boolean {
		// Just some simple verifications. The app currently cannot handle paths, so slash is not allowed
		return !(input.isBlank() || input.contains('/') || input.contains(' '))
	}

	fun isValidOpenAiApiKey(input: String): Boolean {
		// OpenAI API key format: sk- followed by a sequence of uppercase and lowercase letters, digits, dashes, and underscores
		// It is at least 10 characters long
		// Whitespace or empty string is considered invalid (will be interpreted as deleting the key)
		if (input.isBlank()) return false

		val regex = Regex("^sk-[a-zA-Z0-9_-]+$")
		return regex.matches(input) && input.length >= 10
	}
}
