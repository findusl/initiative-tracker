package de.lehrbaum.initiativetracker.ui.screen.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import de.lehrbaum.initiativetracker.ui.model.main.ContentState
import de.lehrbaum.initiativetracker.ui.model.main.DrawerItem
import de.lehrbaum.initiativetracker.ui.model.main.MainModel
import de.lehrbaum.initiativetracker.ui.screen.Constants
import de.lehrbaum.initiativetracker.ui.screen.components.BurgerMenuButtonForDrawer
import de.lehrbaum.initiativetracker.ui.screen.host.HostScreen
import kotlinx.coroutines.launch

@Composable
fun MainScreen(mainModel: MainModel) {
	val scaffoldState = rememberScaffoldState()
	val contentState by mainModel.content
	val drawerItems by mainModel.drawerItems.collectAsState()
	// Theoretically can reduce this to modal drawer
	Scaffold(
		scaffoldState = scaffoldState,
		drawerContent = { Drawer(drawerItems) },
		drawerGesturesEnabled = scaffoldState.drawerState.isOpen,
	) {
		MainScreenContent(contentState, scaffoldState.drawerState)
	}
}

@Composable
private fun Drawer(drawerItems: List<DrawerItem>) {
	Column(Modifier.fillMaxSize()) {
		drawerItems.forEach { item ->
			val backgroundColor = if (item.active) colors.primarySurface else colors.background
			val textColor = if (item.active) colors.onPrimary else colors.onBackground
			Box(Modifier.background(backgroundColor).fillMaxWidth()) {
				Text(item.name, Modifier.padding(Constants.defaultPadding), color = textColor)
			}
		}
	}
}

@Composable
private fun MainScreenContent(contentState: ContentState, drawerState: DrawerState) {
	when(contentState) {
		is ContentState.Empty -> Text("Choose something in the menu.")
		is ContentState.CharacterScreen -> TODO()
		is ContentState.HostCombat -> HostScreen(drawerState, contentState.hostCombatModel)
	}
}
