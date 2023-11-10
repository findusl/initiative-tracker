package de.lehrbaum.initiativetracker.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
		}
	}

}
