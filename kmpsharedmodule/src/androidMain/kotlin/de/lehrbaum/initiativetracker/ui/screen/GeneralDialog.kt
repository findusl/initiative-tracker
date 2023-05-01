package de.lehrbaum.initiativetracker.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog

@Composable
actual fun GeneralDialog(onDismissRequest: () -> Unit, content: @Composable () -> Unit) {
    Dialog(onDismissRequest = onDismissRequest, content = content)
}