package de.lehrbaum.initiativetracker.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ContentScreen(contentModel: ContentModel) {
	val connectionStateState = contentModel.connectionState.collectAsState(false)
	val coroutineScope = rememberCoroutineScope()

	Scaffold(
		topBar = { TopBar(contentModel) },
	) {
		Column {
			Text("This number should match ${contentModel.id}")
			Button(onClick = {
				coroutineScope.launch {
					contentModel.onShareClicked()
				}
			}) {
				Text("Generate new id")
			}
		}
	}

	if (connectionStateState.value) {
		Text("Test")
	}
}

@Composable
private fun TopBar(
	contentModel: ContentModel
) {
	TopAppBar(
		title = {
			Text("This number should match ${contentModel.id}")
		}
	)
}
