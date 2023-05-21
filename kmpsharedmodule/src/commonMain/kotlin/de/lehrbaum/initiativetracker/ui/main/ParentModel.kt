package de.lehrbaum.initiativetracker.ui.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import de.lehrbaum.initiativetracker.ui.host.ContentModel
import de.lehrbaum.initiativetracker.ui.host.ContentModelImpl

class ParentModel {

    var content by mutableStateOf(hostCombat(0))

	private fun hostCombat(sessionId: Int): ContentState {
		val hostCombatModel = ContentModelImpl(sessionId){
			content = hostCombat(it)
		}
		return ContentState.HostCombat(hostCombatModel)
	}

}


sealed interface ContentState {
	data class HostCombat(val contentModel: ContentModel): ContentState
}