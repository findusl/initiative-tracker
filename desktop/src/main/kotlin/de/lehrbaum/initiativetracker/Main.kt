package de.lehrbaum.initiativetracker

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import de.lehrbaum.initiativetracker.ui.model.main.MainModelImpl
import de.lehrbaum.initiativetracker.ui.model.main.MainModelPreview
import de.lehrbaum.initiativetracker.ui.screen.main.MainScreen
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

fun main() = application {
    val windowState = rememberWindowState()

    LaunchedEffect(key1 = this) {
        // Initialize Logging.
        Napier.base(DebugAntilog())
    }

    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
        title = "InitiativeTracker"
    ) {
        MaterialTheme {
            MainScreen(MainModelImpl())
        }
    }
}
