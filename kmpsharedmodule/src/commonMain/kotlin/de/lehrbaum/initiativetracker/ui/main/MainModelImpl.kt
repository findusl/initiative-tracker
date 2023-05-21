package de.lehrbaum.initiativetracker.ui.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import de.lehrbaum.initiativetracker.ui.host.HostLocalCombatModelImpl
import de.lehrbaum.initiativetracker.ui.host.HostSharedCombatModelImpl

class MainModelImpl {

	private val hostCombatState = hostNewCombat()

    var content by mutableStateOf<ContentState>(hostCombatState)

    private fun hostNewCombat(): ContentState.HostCombat {
		val hostCombatModel = HostLocalCombatModelImpl {
			hostCombat(it)
		}
        return ContentState.HostCombat(hostCombatModel)
    }

	private fun hostCombat(sessionId: Int) {
		val hostCombatModel = HostSharedCombatModelImpl(sessionId){
			content = hostCombatState
		}
		content = ContentState.HostCombat(hostCombatModel)
	}

}