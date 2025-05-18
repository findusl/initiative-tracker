package de.lehrbaum.initiativetracker

import androidx.compose.foundation.text.isTypedEvent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.awt.awtEventOrNull
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import de.lehrbaum.initiativetracker.ui.keyevents.LocalShortcutManager
import de.lehrbaum.initiativetracker.ui.keyevents.ShortcutManager
import de.lehrbaum.initiativetracker.ui.main.MainComposable
import de.lehrbaum.initiativetracker.ui.main.MainViewModel
import io.github.aakira.napier.Antilog
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun main() = application {
	val windowState = rememberWindowState(width = 900.dp, height = 600.dp)

	LaunchedEffect(key1 = this) {
		// Initialize Logging.
		Napier.base(CustomAntilog())
	}

	val mainViewModel = MainViewModel()

	val shortcuts = remember { mutableMapOf<Char, () -> Unit>() }
	val shortcutManager = ShortcutManagerImpl(shortcuts)

	Window(
		onCloseRequest = ::exitApplication,
		state = windowState,
		title = "InitiativeTracker",
		onKeyEvent = shortcutManager::onKeyEvent
	) {
		val widthInt: Int? by derivedStateOf {
			windowState.size.width.let { if (it.isSpecified) it.value.toInt() else null }
		}
		CompositionLocalProvider(LocalShortcutManager provides shortcutManager) {
			MainComposable(mainViewModel, widthInt)
		}
	}
}

class CustomAntilog(
	private val defaultTag: String = "App",
	private val minLogLevel: LogLevel = LogLevel.DEBUG
) : Antilog() {


	override fun performLog(
		priority: LogLevel,
		tag: String?,
		throwable: Throwable?,
		message: String?
	) {
		if (priority.ordinal < minLogLevel.ordinal) return
		val formattedTag = tag ?: defaultTag
		val formattedMessage = buildLogMessage(priority, formattedTag, throwable, message)

		when (priority) {
			LogLevel.VERBOSE, LogLevel.DEBUG, LogLevel.INFO, LogLevel.WARNING -> println(formattedMessage)
			LogLevel.ERROR, LogLevel.ASSERT -> System.err.println(formattedMessage)
		}
	}

	private fun buildLogMessage(priority: LogLevel, tag: String, throwable: Throwable?, message: String?): String {
		val logLevelChar = priority.name.first()
		val timestamp = ZonedDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
		val baseMessage = "$logLevelChar $timestamp $tag: $message"

		return baseMessage + (throwable?.let { "\n${it.stackTraceToString()}" } ?: "")
	}
}

private class ShortcutManagerImpl(private val shortcuts: MutableMap<Char, () -> Unit>) : ShortcutManager {
	override fun addShortcut(key: Char, action: () -> Unit): Boolean {
		if (shortcuts.containsKey(key)) return false
		shortcuts[key] = action
		return true
	}

	override fun removeShortcut(key: Char) {
		shortcuts.remove(key)
	}

	fun onKeyEvent(keyEvent: KeyEvent): Boolean {
		Napier.v("Got keyEvent $keyEvent")
		if (keyEvent.isTypedEvent) {
			// Only the typed event has a keyChar
			val key = keyEvent.awtEventOrNull?.keyChar ?: return false
			shortcuts[key]?.let {
				Napier.i("Triggered Shortcut $key")
				it()
				return true
			}
		}
		return false
	}
}
