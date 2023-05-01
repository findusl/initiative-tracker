package de.lehrbaum.initiativetracker.ui.screen

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog

@Composable
actual fun GeneralDialog(onDismissRequest: () -> Unit, content: @Composable () -> Unit) {
    Dialog(onCloseRequest = onDismissRequest) { content() }
}

@Composable
actual fun FullscreenDialog(onDismissRequest: () -> Unit, content: @Composable () -> Unit) {
    Text("Fullscreen dialog does not work on desktop")
}