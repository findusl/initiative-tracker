package de.lehrbaum.initiativetracker.ui.host

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import de.lehrbaum.initiativetracker.ui.Constants
import de.lehrbaum.initiativetracker.ui.GeneralDialog
import de.lehrbaum.initiativetracker.ui.composables.OkCancelButtonRow
import kotlinx.coroutines.launch

@Composable
fun BackendInputDialog(backendInputViewModel: BackendInputViewModel) {
	val coroutineScope = rememberCoroutineScope()
	GeneralDialog(backendInputViewModel.onDismiss) {
		Column(modifier = Modifier.padding(Constants.defaultPadding)) {
			OutlinedTextField(
				value = backendInputViewModel.hostFieldContent,
				onValueChange = { input ->
					backendInputViewModel.hostFieldContent = input
				},
				label = { Text("Host") },
				isError = backendInputViewModel.hostFieldError,
				modifier = Modifier.fillMaxWidth(),
			)
			Row {
				Switch(
					checked = backendInputViewModel.secureConnectionChosen,
					onCheckedChange = { backendInputViewModel.secureConnectionChosen = it }
				)
				Text("Use Secure Connection?")
			}
			OkCancelButtonRow(
				backendInputViewModel.inputsAreValid,
				backendInputViewModel.onDismiss,
				onSubmit = { coroutineScope.launch { backendInputViewModel.onConnectPressed() } },
				backendInputViewModel.isSubmitting,
			)
		}
	}
}
