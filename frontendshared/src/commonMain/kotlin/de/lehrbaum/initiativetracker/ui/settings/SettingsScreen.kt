package de.lehrbaum.initiativetracker.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.DrawerState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.lehrbaum.initiativetracker.ui.Constants
import de.lehrbaum.initiativetracker.ui.composables.BurgerMenuButtonForDrawer

@Composable
fun SettingsScreen(drawerState: DrawerState, settingsViewModel: SettingsViewModel) {
	Scaffold(
		topBar = {
			TopAppBar(
				title = {
					Text("Settings", color = MaterialTheme.colors.onPrimary)
				},
				navigationIcon = { BurgerMenuButtonForDrawer(drawerState) },
				actions = {
					Button(onClick = settingsViewModel::onSavePressed, enabled = settingsViewModel.inputsAreValid) {
						Text("Save")
					}
				}
			)
		}
	) {
		Column(modifier = Modifier.padding(Constants.defaultPadding)) {
			Text("Specify default backend")
			OutlinedTextField(
				value = settingsViewModel.hostFieldContent,
				onValueChange = { input ->
					settingsViewModel.hostFieldContent = input
				},
				label = { Text("Host") },
				isError = settingsViewModel.hostFieldError,
				modifier = Modifier.fillMaxWidth()
			)
			Row {
				Switch(
					checked = settingsViewModel.secureConnectionChosen,
					onCheckedChange = { settingsViewModel.secureConnectionChosen = it }
				)
				Text("Use Secure Connection?")
			}

			Spacer(modifier = Modifier.height(16.dp))

			Text("OpenAI API Key")
			OutlinedTextField(
				value = settingsViewModel.apiKeyFieldContent,
				onValueChange = { input ->
					settingsViewModel.apiKeyFieldContent = input
				},
				label = { Text("API Key") },
				isError = settingsViewModel.apiKeyFieldError,
				modifier = Modifier.fillMaxWidth()
			)
			if (settingsViewModel.apiKeyFieldError) {
				Text(
					"API key must start with 'sk-' followed by letters and digits, at least 10 characters long",
					color = MaterialTheme.colors.error,
					style = MaterialTheme.typography.caption
				)
			} else {
				Text(
					"Leave blank to remove the API key",
					style = MaterialTheme.typography.caption
				)
			}

			Spacer(modifier = Modifier.height(24.dp))

			// Guide management section
			Text("Guide Management", style = MaterialTheme.typography.h6)
			Text(
				"Control the visibility of all beginner guides in the application",
				style = MaterialTheme.typography.caption
			)
			Spacer(modifier = Modifier.height(8.dp))

			Row(modifier = Modifier.fillMaxWidth()) {
				Button(
					onClick = settingsViewModel::hideAllGuides,
					modifier = Modifier.weight(1f).padding(end = 8.dp)
				) {
					Text("Hide All Guides")
				}

				Button(
					onClick = settingsViewModel::showAllGuides,
					modifier = Modifier.weight(1f).padding(start = 8.dp)
				) {
					Text("Show All Guides")
				}
			}
		}
	}
}
