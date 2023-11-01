package de.lehrbaum.initiativetracker.ui.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.lehrbaum.initiativetracker.ui.shared.ErrorState
import de.lehrbaum.initiativetracker.ui.shared.ErrorStateHolder

@Composable
fun ErrorAlertDialog(errorState: ErrorState, onDismissRequest: () -> Unit) {
	val text = errorState.customMessage ?: errorState.failure?.message ?: "An error occurred"
	AlertDialog(
		onDismissRequest,
		buttons = {
			Button(onClick = onDismissRequest, modifier = Modifier.fillMaxWidth()) {
				Text("OK")
			}
			// add option to copy detailed error
		},
		text = {
			Text(text)
		}
	)
}

@Composable
fun ErrorStateHolder.ErrorComposable() {
	errorState?.let {
		ErrorAlertDialog(it) { errorState = null }
	}
}
