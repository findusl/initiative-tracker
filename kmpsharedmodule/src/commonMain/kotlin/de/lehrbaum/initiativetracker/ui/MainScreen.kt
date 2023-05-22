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
fun MainScreen(mainModel: MainModel) = ContentScreen(mainModel.content)

class MainModel {
    var content by mutableStateOf(getContentModel(0))

	private fun getContentModel(sessionId: Int): ContentModel {
		return ContentModel(sessionId) {
			content = getContentModel(Random.nextInt(10000))
		}
	}
}

@Composable
fun ContentScreen(contentModel: ContentModel) {
	Scaffold(topBar = { TopBar(contentModel) },) {
		Column {
			Text("This number should match ${contentModel.id}")
			Button(onClick = contentModel.nextModel) { Text("Generate new id") }
		}
	}

	// this is necessary probably compose scoping
	contentModel.connectionState.collectAsState(false).value.toString()
}

@Composable
private fun TopBar(contentModel: ContentModel) =
	TopAppBar(title = { Text("This number should match ${contentModel.id}") })

data class ContentModel(val id: Int, val nextModel: () -> Unit)  {
	val connectionState: Flow<Boolean>
		get() = flow {
			this.emit(false)
			delay(10)
			this.emit(true)
		}
			//.flowOn(Dispatchers.IO) //no difference
}
