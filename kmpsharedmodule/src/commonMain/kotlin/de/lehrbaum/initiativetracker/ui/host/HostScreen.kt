package de.lehrbaum.initiativetracker.ui.host

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import de.lehrbaum.initiativetracker.bl.HostConnectionState
import kotlinx.coroutines.launch

@Composable
fun HostScreen(hostCombatModel: HostCombatModel) {
	val connectionStateState = hostCombatModel.hostConnectionState.collectAsState(HostConnectionState.Connecting)

	Scaffold(
		topBar = { TopBar(hostCombatModel) },
	) {
		if (hostCombatModel.isSharing) {
			Text("Session ${hostCombatModel.sessionId}")
		} else {
			Text("New combat")
		}
	}

	if (connectionStateState.value == HostConnectionState.Connected) {
		Text("Test")
	}
}

@Composable
private fun TopBar(
	hostCombatModel: HostCombatModel
) {
	val coroutineScope = rememberCoroutineScope()

	TopAppBar(
		title = {
			if (hostCombatModel.isSharing) {
				Text("Session ${hostCombatModel.sessionId}")
			} else {
				Text("New combat")
			}
		},
		actions = {
			if (hostCombatModel.isSharing) {
				IconButton(onClick = {
					coroutineScope.launch {
						hostCombatModel.closeSession()
					}
				}) {
					Icon(Icons.Default.Close, contentDescription = "Close Session")
				}
			} else {
				IconButton(onClick = {
					coroutineScope.launch {
						hostCombatModel.onShareClicked()
					}
				}) {
					Icon(Icons.Default.Share, contentDescription = "Start Sharing")
				}
			}
		},
		backgroundColor = MaterialTheme.colors.primarySurface,
	)
}
