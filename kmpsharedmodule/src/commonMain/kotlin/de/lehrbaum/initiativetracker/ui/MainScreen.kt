package de.lehrbaum.initiativetracker.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlin.random.Random

@Composable
fun MainScreen(mainModel: MainModel) {
	ContentScreen(mainModel.content)
}

class MainModel {

    var content by mutableStateOf(getContentModel(0))

	private fun getContentModel(sessionId: Int): ContentModel {
		return ContentModel(sessionId) {
			content = getContentModel(Random.nextInt(10000))
		}
	}

}