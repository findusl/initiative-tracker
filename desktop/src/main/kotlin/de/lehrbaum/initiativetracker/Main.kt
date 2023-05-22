package de.lehrbaum.initiativetracker

import androidx.compose.material.MaterialTheme
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import de.lehrbaum.initiativetracker.ui.MainScreen

fun main() = application {

	Window(
		onCloseRequest = ::exitApplication,
		title = "InitiativeTracker"
	) {
		MaterialTheme {
			MainScreen()
		}
	}
}
