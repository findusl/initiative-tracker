package de.lehrbaum.initiativetracker.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState

@Composable
fun ContentScreen(contentModel: ContentModel) {

	Scaffold(
		topBar = { TopBar(contentModel) },
	) {
		Column {
			Text("This number should match ${contentModel.id}")
			Button(onClick = contentModel.nextModel) { Text("Generate new id") }
		}
	}

	// this is necessary probably compose scoping
	contentModel.connectionState.collectAsState(false).value.toString()
}

@Composable
private fun TopBar(
	contentModel: ContentModel
) {
	TopAppBar(title = { Text("This number should match ${contentModel.id}") })
}
