package de.lehrbaum.initiativetracker.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

@Composable
fun MainScreen() {
	var id by remember { mutableStateOf(0) }
	val contentModel by remember { derivedStateOf { ContentModel(id) } }

	ContentScreen(contentModel) {
		id = Random.nextInt(1000)
	}
}

@Composable
fun ContentScreen(contentModel: ContentModel, nextModel: () -> Unit) {
	Scaffold(topBar = { TopBar(contentModel) }) {
		Column {
			Text("This number should match ${contentModel.id}")
			Button(onClick = nextModel) { Text("Generate new id") }
		}
	}

	// access flow is necessary
	contentModel.connectionState.collectAsState(false).value.toString()
}

@Composable
private fun TopBar(contentModel: ContentModel) =
	TopAppBar(title = { Text("This number should match ${contentModel.id}") })

data class ContentModel(val id: Int)  {
	/** This flow represents data fetched in the background based on the value of id */
	val connectionState: Flow<Int>
		get() = flow {
			this.emit(id)
			delay(10)
			this.emit(id)
		}
			//.flowOn(Dispatchers.IO) //no difference
}
