package de.lehrbaum.initiativetracker.ui.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import de.lehrbaum.initiativetracker.ui.Constants
import de.lehrbaum.initiativetracker.ui.character.CharacterListScreen
import de.lehrbaum.initiativetracker.ui.client.ClientScreen
import de.lehrbaum.initiativetracker.ui.composables.KeepScreenOn
import de.lehrbaum.initiativetracker.ui.host.HostScreen
import de.lehrbaum.initiativetracker.ui.join.JoinScreen
import de.lehrbaum.initiativetracker.ui.shared.BackHandler
import kotlinx.coroutines.launch

@Composable
fun MainScreen(mainViewModel: MainViewModel, widthInt: Int? = null) {
	val drawerState = rememberDrawerState(DrawerValue.Closed)
	val drawerItems by mainViewModel.drawerItems.collectAsState(emptyList())

	// Workaround for resizing window on desktop
	LaunchedEffect(widthInt) {
		if (widthInt != null) {
			drawerState.snapTo(drawerState.currentValue)
		}
	}

	LaunchedEffect(mainViewModel) {
		mainViewModel.initializeCache(this)
	}

	BackHandler(mainViewModel.canStepBack) {
		mainViewModel.onBackPressed()
	}

	ModalNavigationDrawer(
		drawerState = drawerState,
		drawerContent = {
			Drawer(drawerItems, mainViewModel.content.drawerItem, drawerState, mainViewModel::onDrawerItemSelected)
		},
		gesturesEnabled = drawerState.isOpen,
	) {
		MainScreenContent(mainViewModel.content, drawerState)
	}
}

@Composable
private fun Drawer(
	drawerItems: List<DrawerItem>,
	activeDrawerItem: DrawerItem,
	drawerState: DrawerState,
	onSelected: (DrawerItem) -> Unit
) {
	val coroutineScope = rememberCoroutineScope()
	Column(Modifier.fillMaxSize()) {
		drawerItems.forEach { item ->
			val active = item == activeDrawerItem
			val backgroundColor = if (active) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.background
			val textColor = if (active) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground
			Box(
				Modifier
					.background(backgroundColor)
					.fillMaxWidth()
					.clickable {
						onSelected(item)
						coroutineScope.launch {
							drawerState.close()
						}
					}
			) {
				Text(item.name, Modifier.padding(Constants.defaultPadding), color = textColor)
			}
		}
	}
}

@Composable
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
private fun MainScreenContent(contentState: ContentState, drawerState: DrawerState) {
	if (contentState.keepScreenOn) {
		KeepScreenOn()
	}
	when(contentState) {
		is ContentState.CharacterScreen -> CharacterListScreen(drawerState, contentState.characterListViewModel)
		is ContentState.HostCombat -> HostScreen(drawerState, contentState.hostCombatViewModel)
		is ContentState.ClientCombat -> ClientScreen(drawerState, contentState.clientCombatViewModel)
		is ContentState.JoinCombat ->
			JoinScreen(drawerState, contentState.onJoin, contentState.onCancel, contentState.asHost)
	}
}
