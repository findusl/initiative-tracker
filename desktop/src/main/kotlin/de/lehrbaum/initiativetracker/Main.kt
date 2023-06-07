package de.lehrbaum.initiativetracker

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import de.lehrbaum.initiativetracker.ui.main.MainModelImpl
import de.lehrbaum.initiativetracker.ui.main.MainScreen
import de.lehrbaum.initiativetracker.ui.shared.ProvideScreenSizeInformation
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import java.util.logging.Level
import java.util.logging.LogRecord
import java.util.logging.SimpleFormatter
import java.util.logging.StreamHandler

fun main() = application {
	val windowState = rememberWindowState(width = 900.dp, height = 600.dp)

	LaunchedEffect(key1 = this) {
		val handlers = listOf(StandardConsoleHandler(), ErrorConsoleHandler())
		// Initialize Logging.
		Napier.base(DebugAntilog(handler = handlers))
	}

	Window(
		onCloseRequest = ::exitApplication,
		state = windowState,
		title = "InitiativeTracker"
	) {
		val width = windowState.size.width.let { if (it.isSpecified) it.value.toInt() else 0 }
		val height = windowState.size.height.let { if (it.isSpecified) it.value.toInt() else 0 }
		val widthInt: Int? by derivedStateOf {
			windowState.size.width.let { if (it.isSpecified) it.value.toInt() else null }
		}
		ProvideScreenSizeInformation(width, height) {
			MaterialTheme {
				MainScreen(MainModelImpl(), widthInt)
			}
		}
	}
}

private class StandardConsoleHandler : StreamHandler(System.out, SimpleFormatter()) {
	override fun publish(record: LogRecord?) {
		if (record == null) return
		if (record.level.intValue() >= Level.WARNING.intValue()) return
		super.publish(record)
		flush()
	}

	override fun close() {
		flush()
	}
}

private class ErrorConsoleHandler : StreamHandler(System.err, SimpleFormatter()) {

	init {
		level = Level.WARNING
	}

	override fun publish(record: LogRecord?) {
		super.publish(record)
		flush()
	}

	override fun close() {
		flush()
	}
}
