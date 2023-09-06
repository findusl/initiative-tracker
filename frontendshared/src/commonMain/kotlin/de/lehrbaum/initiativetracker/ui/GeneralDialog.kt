package de.lehrbaum.initiativetracker.ui

import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog

@Composable
fun GeneralDialog(onDismissRequest: () -> Unit, content: @Composable () -> Unit) {
	Dialog(onDismissRequest = onDismissRequest) {
		Surface(content = content)
	}
}
