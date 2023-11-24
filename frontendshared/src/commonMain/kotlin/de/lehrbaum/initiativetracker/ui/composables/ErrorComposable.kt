package de.lehrbaum.initiativetracker.ui.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.lehrbaum.initiativetracker.ui.shared.ErrorStateHolder

@Composable
fun ErrorAlertDialog(message: String, onDismissRequest: () -> Unit) {
	AlertDialog(
		onDismissRequest,
		buttons = {
			Button(onClick = onDismissRequest, modifier = Modifier.fillMaxWidth()) {
				Text("OK")
			}
			// add option to copy detailed error
		},
		text = {
			Text(message)
		}
	)
}

@Composable
fun ErrorStateHolder.ErrorComposable() {
	errorState?.let { errorState ->
		val message = errorState.customMessage ?: errorState.failure?.message ?: "An error occurred"
		ErrorAlertDialog(message) { this.errorState = null }
	}
}
