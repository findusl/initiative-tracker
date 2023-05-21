package de.lehrbaum.initiativetracker.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class ParentModel {

    var content by mutableStateOf(getContentModel(0))

	private fun getContentModel(sessionId: Int): ContentModel {
		return ContentModel(sessionId) { newId ->
			content = getContentModel(newId)
		}
	}

}
