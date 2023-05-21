package de.lehrbaum.initiativetracker.ui.host

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import de.lehrbaum.initiativetracker.bl.HostConnectionState
import kotlinx.coroutines.launch

@Composable
fun HostScreen(contentModel: ContentModel) {
	val connectionStateState = contentModel.hostConnectionState.collectAsState(HostConnectionState.Connecting)

	Scaffold(
		topBar = { TopBar(contentModel) },
	) {
		Text("This number should match ${contentModel.id}")
	}

	if (connectionStateState.value == HostConnectionState.Connected) {
		Text("Test")
	}
}

@Composable
private fun TopBar(
	contentModel: ContentModel
) {
	val coroutineScope = rememberCoroutineScope()

	TopAppBar(
		title = {
			Text("This number should match ${contentModel.id}")
		},
		actions = {
			IconButton(onClick = {
				coroutineScope.launch {
					contentModel.onShareClicked()
				}
			}) {
				Icon(Icons.Default.Share, contentDescription = "Start Sharing")
			}
		},
		backgroundColor = MaterialTheme.colors.primarySurface,
	)
}
