package de.lehrbaum.initiativetracker.platform

import io.github.aakira.napier.Napier
import java.awt.Desktop
import java.net.URI

/**
 * JVM implementation of openPlatformUrl that uses java.awt.Desktop to open URLs
 */
actual fun openPlatformUrl(url: String) {
	try {
		val uri = URI(url)
		val desktop = if (Desktop.isDesktopSupported()) Desktop.getDesktop() else null
		if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			desktop.browse(uri)
		}
	} catch (e: Exception) {
		// Handle exception (could log it in a real application)
		Napier.e("Error opening URL", e)
	}
}