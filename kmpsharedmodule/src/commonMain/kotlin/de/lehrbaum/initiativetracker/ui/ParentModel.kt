package de.lehrbaum.initiativetracker.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class ParentModel {

    var content by mutableStateOf(hostCombat(0))

	private fun hostCombat(sessionId: Int): ContentState {
		val hostCombatModel = ContentModel(sessionId){
			content = hostCombat(it)
		}
		return ContentState.HostCombat(hostCombatModel)
	}

}


sealed interface ContentState {
	data class HostCombat(val contentModel: ContentModel): ContentState
}