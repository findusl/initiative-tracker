package de.lehrbaum.initiativetracker.ui.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import de.lehrbaum.initiativetracker.ui.Constants
import de.lehrbaum.initiativetracker.ui.character.CharacterListScreen
import de.lehrbaum.initiativetracker.ui.client.ClientScreen
import de.lehrbaum.initiativetracker.ui.host.HostScreen
import de.lehrbaum.initiativetracker.ui.join.JoinScreen
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

	ModalDrawer(
		drawerState = drawerState,
		drawerContent = {
			Drawer(drawerItems, mainViewModel.activeDrawerItem, drawerState, mainViewModel::onDrawerItemSelected)
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
			val backgroundColor = if (active) colors.primarySurface else colors.background
			val textColor = if (active) colors.onPrimary else colors.onBackground
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

@Suppress("OPT_IN_IS_NOT_ENABLED")
@Composable
@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
private fun MainScreenContent(contentState: ContentState, drawerState: DrawerState) {
	when(contentState) {
		is ContentState.Empty -> Text("Choose something in the menu.")
		is ContentState.CharacterScreen -> CharacterListScreen(drawerState, contentState.characterListViewModel)
		is ContentState.HostCombat -> HostScreen(drawerState, contentState.hostCombatViewModel)
		is ContentState.ClientCombat -> ClientScreen(drawerState, contentState.clientCombatViewModel)
		is ContentState.JoinCombat ->
			JoinScreen(drawerState, contentState.onJoin, contentState.onCancel, contentState.asHost)
	}
}
