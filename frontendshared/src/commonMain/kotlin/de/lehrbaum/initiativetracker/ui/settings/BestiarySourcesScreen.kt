package de.lehrbaum.initiativetracker.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Link
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import de.lehrbaum.initiativetracker.platform.openPlatformUrl
import de.lehrbaum.initiativetracker.ui.Constants

@Composable
fun BestiarySourcesSettingsScreen(settingsViewModel: SettingsViewModel) {
	Scaffold(
		topBar = { TopBar(settingsViewModel) },
	) {
		LazyColumn(modifier = Modifier.padding(Constants.defaultPadding)) {
			item {
				// Homebrew JSON links section
				Text("Homebrew JSON Resources", style = MaterialTheme.typography.h6)
				Text(
					"Add links to homebrew JSON resources compatible with 5e.tools",
					style = MaterialTheme.typography.caption,
				)

				// GitHub reference link
				Row(
					verticalAlignment = Alignment.CenterVertically,
					modifier = Modifier
						.clickable {
							openPlatformUrl("https://github.com/TheGiddyLimit/homebrew/tree/master/creature")
						}.padding(vertical = 4.dp),
				) {
					Icon(
						Icons.Default.Link,
						contentDescription = "Link",
						tint = MaterialTheme.colors.primary,
						modifier = Modifier.padding(end = 4.dp),
					)
					Text(
						"Find more homebrew JSONs on GitHub",
						color = MaterialTheme.colors.primary,
						textDecoration = TextDecoration.Underline,
					)
				}

				Spacer(modifier = Modifier.height(8.dp))

				// Add new link input
				Row(modifier = Modifier.fillMaxWidth()) {
					OutlinedTextField(
						value = settingsViewModel.newHomebrewLinkContent,
						onValueChange = { settingsViewModel.newHomebrewLinkContent = it },
						label = { Text("JSON URL") },
						isError = settingsViewModel.newHomebrewLinkError,
						modifier = Modifier.weight(1f),
					)
					IconButton(
						onClick = { settingsViewModel.addHomebrewLink() },
						enabled = settingsViewModel.newHomebrewLinkContent.isNotBlank() && !settingsViewModel.newHomebrewLinkError,
					) {
						Icon(Icons.Default.Add, contentDescription = "Add")
					}
				}
				if (settingsViewModel.newHomebrewLinkError) {
					Text(
						"Please enter a valid URL",
						color = MaterialTheme.colors.error,
						style = MaterialTheme.typography.caption,
					)
				}

				Spacer(modifier = Modifier.height(8.dp))
			}

			// List of existing links
			items(settingsViewModel.homebrewLinks) { link ->
				Card(
					modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
					elevation = 2.dp,
				) {
					Row(
						modifier = Modifier.fillMaxWidth().padding(8.dp),
					) {
						Text(
							text = link,
							color = MaterialTheme.colors.primary,
							textDecoration = TextDecoration.Underline,
							modifier = Modifier
								.weight(1f)
								.clickable { openPlatformUrl(link) },
						)
						IconButton(onClick = { settingsViewModel.removeHomebrewLink(link) }) {
							Icon(Icons.Default.Delete, contentDescription = "Remove")
						}
					}
				}
			}
		}
	}
}

@Composable
private fun TopBar(settingsViewModel: SettingsViewModel) {
	TopAppBar(
		title = {
			Text("Bestiary Sources", color = MaterialTheme.colors.onPrimary)
		},
		navigationIcon = {
			IconButton(onClick = { settingsViewModel.closeSourcesSubmenu() }) {
				Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", modifier = Modifier.padding(8.dp))
			}
		},
	)
}
