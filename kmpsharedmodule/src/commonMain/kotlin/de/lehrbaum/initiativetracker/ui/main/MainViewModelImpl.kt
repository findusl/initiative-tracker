package de.lehrbaum.initiativetracker.ui.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import de.lehrbaum.initiativetracker.GlobalInstances
import de.lehrbaum.initiativetracker.bl.data.CombatLink
import de.lehrbaum.initiativetracker.bl.data.CombatLinkRepository
import de.lehrbaum.initiativetracker.networking.BestiaryNetworkClient
import de.lehrbaum.initiativetracker.ui.character.CharacterListViewModelImpl
import de.lehrbaum.initiativetracker.ui.client.ClientCombatViewModelImpl
import de.lehrbaum.initiativetracker.ui.host.HostLocalCombatViewModelImpl
import de.lehrbaum.initiativetracker.ui.host.HostSharedCombatViewModelImpl
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MainViewModelImpl: MainViewModel {
	override var activeDrawerItem by mutableStateOf<DrawerItem>(DrawerItem.HostCombat)

	private val defaultDrawerItems = listOf(
		DrawerItem.JoinCombat,
		DrawerItem.HostCombat,
		DrawerItem.HostExistingCombat,
		DrawerItem.Characters
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
            is DrawerItem.Characters -> ContentState.CharacterScreen(CharacterListViewModelImpl())
            is DrawerItem.HostCombat -> hostCombatState
            is DrawerItem.HostExistingCombat -> joinCombat(asHost = true)
            is DrawerItem.JoinCombat -> joinCombat(asHost = false)
            is DrawerItem.RememberedCombat -> {
				if(item.isHost) hostCombat(item.id) else clientCombat(item.id)
			}
        }
		activeDrawerItem = item
		content = newContent
	}

	override fun initializeCache(scope: CoroutineScope) {
		Napier.i("Initializing Cache")
		scope.launch {
			val monsters = BestiaryNetworkClient(GlobalInstances.httpClient).monsters.first()
			MainViewModel.monsters.emit(monsters)
		}
	}

	private fun hostNewCombat(): ContentState.HostCombat {
		val hostCombatModel = HostLocalCombatViewModelImpl {
			switchToCombat(it, asHost = true)
		}
        return ContentState.HostCombat(hostCombatModel)
    }

	private fun clientCombat(sessionId: Int): ContentState.ClientCombat {
		val model = ClientCombatViewModelImpl(sessionId) {
			onDrawerItemSelected(DrawerItem.HostCombat)
		}
		return ContentState.ClientCombat(model)
	}

	private fun hostCombat(sessionId: Int): ContentState.HostCombat {
		val hostCombatModel = HostSharedCombatViewModelImpl(sessionId){
			onDrawerItemSelected(DrawerItem.HostCombat)
		}
		return ContentState.HostCombat(hostCombatModel)
	}

	private fun joinCombat(asHost: Boolean): ContentState.JoinCombat {
		return ContentState.JoinCombat(
			onJoin = { switchToCombat(it, asHost) },
			onCancel = { onDrawerItemSelected(DrawerItem.HostCombat) },
			asHost
		)
	}

	private fun switchToCombat(sessionId: Int, asHost: Boolean) {
		CombatLinkRepository.addCombatLink(CombatLink(sessionId, asHost))
		// At this point the item might not yet be visible in the drawer, but that should not matter
		onDrawerItemSelected(DrawerItem.RememberedCombat(sessionId, asHost))
	}
}