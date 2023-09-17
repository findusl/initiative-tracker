package de.lehrbaum.initiativetracker.bl

import io.ktor.http.URLBuilder

object InputValidator {
	fun isValidHost(input: String): Boolean {
		if(input.isBlank()) return false
		val urlWithSchema = "https://$input"
		try {
			val urlBuilder = URLBuilder("https://$input")
			// the URLBuilder will auto-fix some stuff, this makes sure nothing was removed as invalid
			return urlBuilder.buildString() == urlWithSchema
		} catch (e: Exception) { return false }
	}
}