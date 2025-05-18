package de.lehrbaum.initiativetracker.platform

import android.content.Intent
import androidx.core.net.toUri
import de.lehrbaum.initiativetracker.ContextHolder
import io.github.aakira.napier.Napier

actual fun openPlatformUrl(url: String) {
	try {
		val intent = Intent(Intent.ACTION_VIEW, url.toUri())
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
		ContextHolder.appContext.startActivity(intent)
	} catch (e: Exception) {
		Napier.e("Error opening URL", e)
	}
}
