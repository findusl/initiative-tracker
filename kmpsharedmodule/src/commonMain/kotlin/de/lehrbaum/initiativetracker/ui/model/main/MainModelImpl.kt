package de.lehrbaum.initiativetracker.ui.model.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import de.lehrbaum.initiativetracker.bl.CombatLink
import de.lehrbaum.initiativetracker.bl.CombatLinkRepository
import de.lehrbaum.initiativetracker.ui.model.host.HostCombatModelImpl
import kotlinx.coroutines.flow.map

class MainModelImpl: MainModel {
	override var activeDrawerItem by mutableStateOf<DrawerItem>(DrawerItem.HostCombat)

	private val defaultDrawerItems = listOf(
		DrawerItem.JoinCombat,
		DrawerItem.HostCombat,
		DrawerItem.HostExistingCombat,
		DrawerItem.Characters
	)

	override val drawerItems = CombatLinkRepository.combatLinks.map {  combatLinks ->
		defaultDrawerItems + combatLinks.map { DrawerItem.RememberedCombat(it.combatId, it.isHost) }
	}

	/** Keep a default hostCombatState to return to */
	private val hostCombatState = hostNewCombat()

    override var content by mutableStateOf<ContentState>(hostCombatState)

    override fun onDrawerItemSelected(item: DrawerItem) {
		if (item == activeDrawerItem) return // avoid double click race conditions
        val newContent: ContentState? = when (item) {
            is DrawerItem.Characters -> null
            is DrawerItem.HostCombat -> hostNewCombat()
            is DrawerItem.HostExistingCombat -> TODO()
            is DrawerItem.JoinCombat -> joinCombat()
            is DrawerItem.RememberedCombat -> {
				if(item.isHost) TODO() else ContentState.ClientCombat(TODO())
			}
        }
		if (newContent != null) {
			activeDrawerItem = item
			content.cancelContent()
			content = newContent
		}
    }

    private fun hostNewCombat(): ContentState.HostCombat {

        return ContentState.HostCombat(HostCombatModelImpl())
    }

	private fun joinCombat(): ContentState.JoinCombat {
		return ContentState.JoinCombat(
			onJoin = { joinCombat(it, false) },
			onCancel = { onDrawerItemSelected(DrawerItem.HostCombat) })
	}

	private fun joinCombat(sessionId: Int, asHost: Boolean) {
		CombatLinkRepository.addCombatLink(CombatLink(sessionId, asHost))
		// At this point the item might not yet be visible in the drawer, but that should not matter
		onDrawerItemSelected(DrawerItem.RememberedCombat(sessionId, asHost))
	}

	private fun ContentState.cancelContent() {
		// TODO implement once some contents are cancellable
	}
}