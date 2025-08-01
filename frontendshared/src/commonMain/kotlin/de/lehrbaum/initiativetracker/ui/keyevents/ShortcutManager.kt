package de.lehrbaum.initiativetracker.ui.keyevents

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.compositionLocalOf

val LocalShortcutManager = compositionLocalOf<ShortcutManager?> { null }

interface ShortcutManager {
	fun addShortcut(key: Char, action: () -> Unit): Boolean

	fun removeShortcut(key: Char)
}

@Composable
fun ShortcutManager.disposableShortcut(key: Char, action: () -> Unit) {
	DisposableEffect(Unit) {
		if (addShortcut(key, action)) {
			onDispose { removeShortcut(key) }
		} else {
			onDispose { }
		}
	}
}
