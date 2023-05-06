package de.lehrbaum.initiativetracker.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
actual fun GeneralDialog(onDismissRequest: () -> Unit, content: @Composable () -> Unit) {
    Dialog(onDismissRequest = onDismissRequest, content = content)
}

@Composable
actual fun FullscreenDialog(onDismissRequest: () -> Unit, content: @Composable () -> Unit) {
    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = onDismissRequest,
        content = content
    )
}
