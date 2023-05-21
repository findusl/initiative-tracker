package de.lehrbaum.initiativetracker.ui.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import de.lehrbaum.initiativetracker.bl.data.CombatLink
import de.lehrbaum.initiativetracker.bl.data.CombatLinkRepository
import de.lehrbaum.initiativetracker.ui.host.HostLocalCombatModelImpl
import de.lehrbaum.initiativetracker.ui.host.HostSharedCombatModelImpl
import kotlinx.coroutines.flow.map

class MainModelImpl: MainModel {
	override var activeDrawerItem by mutableStateOf<DrawerItem>(DrawerItem.HostCombat)

	private val defaultDrawerItems = listOf(
		DrawerItem.OtherModel,
		DrawerItem.HostCombat,
	)

	override val drawerItems = CombatLinkRepository.combatLinks.map { combatLinks ->
		defaultDrawerItems + combatLinks.map { DrawerItem.RememberedCombat(it.sessionId, it.isHost) }
	}

	/** Keep a default hostCombatState to return to */
	private val hostCombatState = hostNewCombat()

    override var content by mutableStateOf<ContentState>(hostCombatState)

    override fun onDrawerItemSelected(item: DrawerItem) {
		if (item == activeDrawerItem) return // avoid double click race conditions
        val newContent: ContentState = when (item) {
            is DrawerItem.HostCombat -> hostCombatState
            is DrawerItem.RememberedCombat -> {
				hostCombat(item.id)
			}
			DrawerItem.OtherModel -> ContentState.Empty
		}
		activeDrawerItem = item
		content = newContent
	}

    private fun hostNewCombat(): ContentState.HostCombat {
		val hostCombatModel = HostLocalCombatModelImpl {
			switchToCombat(it, asHost = true)
		}
        return ContentState.HostCombat(hostCombatModel)
    }

	private fun hostCombat(sessionId: Int): ContentState.HostCombat {
		val hostCombatModel = HostSharedCombatModelImpl(sessionId){
			onDrawerItemSelected(DrawerItem.HostCombat)
		}
		return ContentState.HostCombat(hostCombatModel)
	}

	private fun switchToCombat(sessionId: Int, asHost: Boolean) {
		CombatLinkRepository.addCombatLink(CombatLink(sessionId, asHost))
		// At this point the item might not yet be visible in the drawer, but that should not matter
		onDrawerItemSelected(DrawerItem.RememberedCombat(sessionId, asHost))
	}
}