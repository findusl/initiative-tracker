package de.lehrbaum.initiativetracker.ui.screen.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import de.lehrbaum.initiativetracker.ui.model.main.ContentState
import de.lehrbaum.initiativetracker.ui.model.main.DrawerItem
import de.lehrbaum.initiativetracker.ui.model.main.MainModel
import de.lehrbaum.initiativetracker.ui.screen.Constants
import de.lehrbaum.initiativetracker.ui.screen.client.ClientScreen
import de.lehrbaum.initiativetracker.ui.screen.host.HostScreen
import de.lehrbaum.initiativetracker.ui.screen.join.JoinScreen
import kotlinx.coroutines.launch

@Composable
fun MainScreen(mainModel: MainModel) {
	val scaffoldState = rememberScaffoldState()
	val drawerItems by mainModel.drawerItems.collectAsState(emptyList())
	// Theoretically can reduce this to modal drawer
	Scaffold(
		scaffoldState = scaffoldState,
		drawerContent = {
			Drawer(drawerItems, mainModel.activeDrawerItem, scaffoldState.drawerState, mainModel::onDrawerItemSelected)
		},
		drawerGesturesEnabled = scaffoldState.drawerState.isOpen,
	) {
		MainScreenContent(mainModel.content, scaffoldState.drawerState)
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

@Composable
@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
private fun MainScreenContent(contentState: ContentState, drawerState: DrawerState) {
	when(contentState) {
		is ContentState.Empty -> Text("Choose something in the menu.")
		is ContentState.CharacterScreen -> TODO()
		is ContentState.HostCombat -> HostScreen(drawerState, contentState.hostCombatModel)
		is ContentState.ClientCombat -> ClientScreen(drawerState, contentState.clientCombatModel)
		is ContentState.JoinCombat -> JoinScreen(drawerState, contentState.onJoin, contentState.onCancel)
	}
}
