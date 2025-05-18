package de.lehrbaum.initiativetracker.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.DrawerState
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.lehrbaum.initiativetracker.ui.Constants
import de.lehrbaum.initiativetracker.ui.composables.BurgerMenuButtonForDrawer
import de.lehrbaum.initiativetracker.ui.shared.ListDetailLayout

@Composable
fun SettingsScreen(drawerState: DrawerState, settingsViewModel: SettingsViewModel) {
	ListDetailLayout(
		list = { 
			Scaffold(
				topBar = { TopBar(drawerState, settingsViewModel) }
			) {
				MainSettingsScreen(settingsViewModel)
			}
		},
		detail = if (settingsViewModel.isBestiarySubmenuOpen) {
			{ BestiarySourcesSettingsScreen(settingsViewModel) }
		} else null,
		onDetailDismissRequest = { settingsViewModel.closeSourcesSubmenu() }
	)
}

@Composable
private fun TopBar(
	drawerState: DrawerState,
	settingsViewModel: SettingsViewModel
) {
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

@Composable
fun MainSettingsScreen(settingsViewModel: SettingsViewModel) {
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

		Spacer(modifier = Modifier.height(Constants.defaultPadding))

		OutlinedTextField(
			value = settingsViewModel.apiKeyFieldContent,
			onValueChange = { input ->
				settingsViewModel.apiKeyFieldContent = input
			},
			label = { Text("OpenAI API Key (Starts with sk-)") },
			isError = settingsViewModel.apiKeyFieldError,
			modifier = Modifier.fillMaxWidth()
		)
		if (settingsViewModel.apiKeyFieldError) {
			Text(
				"API key does not match the expected format.",
				color = MaterialTheme.colors.error,
				style = MaterialTheme.typography.caption
			)
		} else {
			Text(
				"Used to autogenerate names for monsters based on type",
				style = MaterialTheme.typography.caption
			)
		}

		Spacer(modifier = Modifier.height(Constants.defaultPadding))

		Row(
			modifier = Modifier
				.fillMaxWidth()
				.clickable { settingsViewModel.openSourcesSubmenu() },
			verticalAlignment = Alignment.CenterVertically,
		) {
			Text(
				"Bestiary Sources",
				style = MaterialTheme.typography.h6,
				modifier = Modifier.weight(1f)
			)
			Icon(
				Icons.AutoMirrored.Filled.ArrowForward,
				contentDescription = "Open Bestiary Sources",
				modifier = Modifier.padding(8.dp)
			)
		}

		Spacer(modifier = Modifier.height(Constants.defaultPadding))
		Divider()
		Spacer(modifier = Modifier.height(Constants.defaultPadding))

		Text("Guide Management", style = MaterialTheme.typography.h6)
		Text(
			"Control the visibility of all beginner guides in the application",
			style = MaterialTheme.typography.caption
		)
		Spacer(modifier = Modifier.height(Constants.smallPadding))

		Row(modifier = Modifier.fillMaxWidth()) {
			Button(
				onClick = settingsViewModel::hideAllGuides,
				modifier = Modifier.weight(1f).padding(end = Constants.smallPadding)
			) {
				Text("Hide All Guides")
			}

			Button(
				onClick = settingsViewModel::showAllGuides,
				modifier = Modifier.weight(1f).padding(start = Constants.smallPadding)
			) {
				Text("Show All Guides")
			}
		}
	}
}
