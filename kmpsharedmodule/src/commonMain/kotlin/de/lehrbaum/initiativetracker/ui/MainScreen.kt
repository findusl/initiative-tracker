package de.lehrbaum.initiativetracker.ui

import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable

@Composable
fun MainScreen(mainModel: ParentModel) {
	val scaffoldState = rememberScaffoldState()
	// Theoretically can reduce this to modal drawer
	Scaffold(
		scaffoldState = scaffoldState,
		drawerGesturesEnabled = scaffoldState.drawerState.isOpen,
	) {
		MainScreenContent(mainModel.content)
	}
}

@Composable
private fun MainScreenContent(contentState: ContentState) {
	when(contentState) {
		is ContentState.HostCombat -> ContentScreen(contentState.contentModel)
	}
}
