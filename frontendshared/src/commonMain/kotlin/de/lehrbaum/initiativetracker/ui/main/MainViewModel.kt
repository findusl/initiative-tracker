package de.lehrbaum.initiativetracker.ui.main

import androidx.compose.runtime.*
import de.lehrbaum.initiativetracker.GlobalInstances
import de.lehrbaum.initiativetracker.bl.data.CombatLink
import de.lehrbaum.initiativetracker.bl.data.CombatLinkRepository
import de.lehrbaum.initiativetracker.networking.bestiary.MonsterDTO
import de.lehrbaum.initiativetracker.ui.character.CharacterListViewModel
import de.lehrbaum.initiativetracker.ui.client.ClientCombatViewModel
import de.lehrbaum.initiativetracker.ui.client.ClientCombatViewModelImpl
import de.lehrbaum.initiativetracker.ui.host.HostCombatViewModel
import de.lehrbaum.initiativetracker.ui.host.HostLocalCombatViewModelImpl
import de.lehrbaum.initiativetracker.ui.host.HostSharedCombatViewModelImpl
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Stable // assumes content is backed by mutableState variable. Hope I don't forget
open class MainViewModel() {
	private val defaultDrawerItems = listOf(
		DrawerItem.JoinCombat,
		DrawerItem.HostCombat,
		DrawerItem.HostExistingCombat,
		DrawerItem.Characters
	)
	val drawerItems = CombatLinkRepository.combatLinks.map { combatLinks ->
		defaultDrawerItems + combatLinks.map { DrawerItem.RememberedCombat(it.sessionId, it.isHost) }
	}

	/** Keep a default hostCombatState to return to */
	private val hostCombatState = hostNewCombat()
	var content by mutableStateOf<ContentState>(hostCombatState)
	private val backstack = mutableStateListOf<ContentState>()
	val canStepBack: Boolean
		get() = backstack.isNotEmpty() // since this is backed by a state variable the ui is notified

	fun onDrawerItemSelected(item: DrawerItem) {
		if (item == content.drawerItem) return // avoid double click race conditions
        val newContent: ContentState = when (item) {
            is DrawerItem.Characters -> ContentState.CharacterScreen(CharacterListViewModel())
            is DrawerItem.HostCombat -> hostCombatState
            is DrawerItem.HostExistingCombat -> joinCombat(asHost = true)
            is DrawerItem.JoinCombat -> joinCombat(asHost = false)
            is DrawerItem.RememberedCombat -> {
				if(item.isHost) hostCombat(item.id) else clientCombat(item.id)
			}
        }
		backstack.add(index = 0, content)
		content = newContent
	}

	fun initializeCache(scope: CoroutineScope) {
		Napier.i("Initializing Cache")
		scope.launch {
			GlobalInstances.bestiaryNetworkClient.monsters.collect {
				Cache.monsters = it
			}
			// Completely unnecessary optimization, but I was annoyed that the map would be generated too often
			Cache.monstersByName = Cache.monsters.associateBy(MonsterDTO::displayName)
		}
	}

	fun onBackPressed() {
		content = backstack.removeFirstOrNull() ?: return
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

	object Cache {
		var monsters: List<MonsterDTO> by mutableStateOf(emptyList())
		var monstersByName: Map<String, MonsterDTO>? by mutableStateOf(null)
		fun getMonsterByName(name: String): MonsterDTO? {
			// Fallback while map is not yet loaded. Completely unnecessary optimization
			return monstersByName?.let {
				it.getOrElse(name) { null }
			} ?: monsters.firstOrNull { it.displayName == name }
		}
	}
}

sealed interface DrawerItem {
	val name: String

	object JoinCombat : DrawerItem {
		override val name = "Join Combat"
	}

	object HostCombat : DrawerItem {
		override val name = "Host Combat"
	}

	object HostExistingCombat : DrawerItem {
		override val name = "Host existing Combat"
	}

	object Characters : DrawerItem {
		override val name = "Characters"
	}

	data class RememberedCombat(
		val id: Int,
		val isHost: Boolean,
		override val name: String = id.toString() + if (isHost) " Host" else " Client"
	) : DrawerItem
}

sealed class ContentState(
	val drawerItem: DrawerItem
) {

	data class HostCombat(val hostCombatViewModel: HostCombatViewModel) :
		// FIXME ugly line, consider changing
		ContentState(if (hostCombatViewModel.isSharing) DrawerItem.RememberedCombat(hostCombatViewModel.sessionId, isHost = true) else DrawerItem.HostCombat) {
	}

	data class JoinCombat(val onJoin: (Int) -> Unit, val onCancel: () -> Unit, val asHost: Boolean) :
		ContentState(if (asHost) DrawerItem.HostExistingCombat else DrawerItem.JoinCombat)

	data class ClientCombat(val clientCombatViewModel: ClientCombatViewModel) :
		ContentState(DrawerItem.RememberedCombat(clientCombatViewModel.sessionId, isHost = false)) {
	}

	data class CharacterScreen(val characterListViewModel: CharacterListViewModel) : ContentState(DrawerItem.Characters)
}
