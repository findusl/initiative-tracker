package de.lehrbaum.initiativetracker.ui

import androidx.compose.runtime.Composable

@Composable
fun MainScreen(mainModel: ParentModel) {
	ContentScreen(mainModel.content)
}
