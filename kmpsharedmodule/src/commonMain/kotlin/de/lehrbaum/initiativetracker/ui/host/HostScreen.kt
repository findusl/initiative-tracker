package de.lehrbaum.initiativetracker.ui.host

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import de.lehrbaum.initiativetracker.bl.HostConnectionState
import de.lehrbaum.initiativetracker.ui.composables.BurgerMenuButtonForDrawer
import de.lehrbaum.initiativetracker.ui.edit.EditCombatantDialog
import kotlinx.coroutines.launch

@Composable
@ExperimentalMaterialApi
@ExperimentalFoundationApi
fun HostScreen(drawerState: DrawerState, hostCombatModel: HostCombatModel) {
	val connectionStateState = hostCombatModel.hostConnectionState.collectAsState(HostConnectionState.Connecting)

	Scaffold(
		topBar = { TopBar(drawerState, hostCombatModel) },
	) {
		val connectionState = connectionStateState.value
		if (connectionState != HostConnectionState.Connecting)
			Text("Content")
	}

	if (connectionStateState.value == HostConnectionState.Connected) {
		hostCombatModel.editCombatantModel.value?.let {
			EditCombatantDialog(it)
		}
	}
}

@Composable
private fun TopBar(
	drawerState: DrawerState,
	hostCombatModel: HostCombatModel
) {
	val coroutineScope = rememberCoroutineScope()

	TopAppBar(
		title = {
			if (hostCombatModel.isSharing) {
				Text("Session ${hostCombatModel.sessionId}", color = MaterialTheme.colors.onPrimary)
			} else {
				Text("New combat", color = MaterialTheme.colors.onPrimary)
			}
		},
		navigationIcon = {
			BurgerMenuButtonForDrawer(drawerState)
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
