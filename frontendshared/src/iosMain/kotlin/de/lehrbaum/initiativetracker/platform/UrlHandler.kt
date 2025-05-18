package de.lehrbaum.initiativetracker.platform

import io.github.aakira.napier.Napier
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

/**
 * Opens the URL in the default browser using UIApplication
 */
actual fun openPlatformUrl(url: String) {
	try {
		val nsUrl = NSURL.URLWithString(url) ?: throw IllegalArgumentException("Invalid URL $url")
		UIApplication.sharedApplication.openURL(nsUrl, options = emptyMap<Any?, Any?>(), completionHandler = null)
		return
	} catch (e: Exception) {
		Napier.e("Error opening URL", e)
	}
}
