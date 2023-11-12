package de.lehrbaum.initiativetracker.ui.main

import androidx.compose.runtime.*
import de.lehrbaum.initiativetracker.GlobalInstances
import de.lehrbaum.initiativetracker.bl.MonsterCache
import de.lehrbaum.initiativetracker.bl.data.CombatLink
import de.lehrbaum.initiativetracker.bl.data.CombatLinkRepository
import de.lehrbaum.initiativetracker.networking.bestiary.MonsterDTO
import de.lehrbaum.initiativetracker.ui.character.CharacterListViewModel
import de.lehrbaum.initiativetracker.ui.client.ClientCombatViewModel
import de.lehrbaum.initiativetracker.ui.host.HostCombatViewModel
import de.lehrbaum.initiativetracker.ui.host.HostLocalCombatViewModelImpl
import de.lehrbaum.initiativetracker.ui.host.HostSharedCombatViewModelImpl
import de.lehrbaum.initiativetracker.ui.join.JoinViewModel
import de.lehrbaum.initiativetracker.ui.settings.SettingsViewModel
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Stable // assumes content is backed by mutableState variable. Hope I don't forget
open class MainViewModel {
	private val defaultDrawerItems = listOf(
		DrawerItem.JoinCombat,
		DrawerItem.JoinAsHost,
		DrawerItem.HostCombat,
		DrawerItem.Characters,
		DrawerItem.Settings
	)
	val drawerItems = CombatLinkRepository.combatLinks.map { combatLinks ->
		defaultDrawerItems + combatLinks.map { DrawerItem.RememberedCombat(it) }
	}

	/* Persistent ViewModels */
	private val localCombatContentState = hostNewCombat()
	private val characterContentState = ContentState.CharacterScreen(CharacterListViewModel())
	private val settingsContentState = ContentState.SettingsScreen(SettingsViewModel())
	// Task also make join screen persistent but test first

	var content by mutableStateOf<ContentState>(localCombatContentState)
	private val backstack = mutableStateListOf<ContentState>()
	val canStepBack: Boolean
		get() = backstack.isNotEmpty() // since this is backed by a state variable the ui is notified

	fun onDrawerItemSelected(item: DrawerItem) {
		if (item == content.drawerItem) return // avoid double click race conditions
		val newContent: ContentState = when (item) {
			is DrawerItem.Characters -> characterContentState
			is DrawerItem.HostCombat -> localCombatContentState
			is DrawerItem.JoinAsHost -> joinCombat(asHost = true)
			is DrawerItem.JoinCombat -> joinCombat(asHost = false)
			DrawerItem.Settings -> settingsContentState
			is DrawerItem.RememberedCombat -> {
				if (item.combatLink.isHost) hostCombat(item.combatLink) else clientCombat(item.combatLink)
			}
		}
		backstack.add(index = 0, content)
		content = newContent
	}

	fun initializeCache(scope: CoroutineScope) {
		Napier.i("Initializing Cache")
		scope.launch {
			GlobalInstances.bestiaryNetworkClient.monsters.collect {
				MonsterCache.monsters = it
			}
			MonsterCache.monstersByName = MonsterCache.monsters.associateBy(MonsterDTO::displayName)
		}
	}

	fun onBackPressed() {
		content = backstack.removeFirstOrNull() ?: return
	}

	private fun hostNewCombat(): ContentState.HostCombat {
		val hostCombatModel = HostLocalCombatViewModelImpl {
			switchToCombat(it)
		}
		return ContentState.HostCombat(hostCombatModel)
	}

	private fun clientCombat(combatLink: CombatLink): ContentState.ClientCombat {
		val model = ClientCombatViewModel(combatLink) {
			onDrawerItemSelected(DrawerItem.HostCombat)
		}
		return ContentState.ClientCombat(model)
	}

	private fun hostCombat(combatLink: CombatLink): ContentState.HostCombat {
		val hostCombatModel = HostSharedCombatViewModelImpl(combatLink) {
			onDrawerItemSelected(DrawerItem.HostCombat)
		}
		return ContentState.HostCombat(hostCombatModel)
	}

	private fun joinCombat(asHost: Boolean): ContentState.JoinCombat {
		val viewModel = JoinViewModel(onJoin = { switchToCombat(it) }, asHost)
		return ContentState.JoinCombat(viewModel)
	}

	private fun switchToCombat(combatLink: CombatLink) {
		CombatLinkRepository.addCombatLink(combatLink)
		// At this point the item might not yet be visible in the drawer, but that should not matter
		onDrawerItemSelected(DrawerItem.RememberedCombat(combatLink))
	}
}

sealed interface DrawerItem {
	val name: String

	data object JoinCombat : DrawerItem {
		override val name = "Join Combat"
	}

	data object JoinAsHost : DrawerItem {
		override val name = "Join Combat as Host"
	}

	data object HostCombat : DrawerItem {
		override val name = "Host Combat"
	}

	data object Characters : DrawerItem {
		override val name = "Characters"
	}

	data object Settings : DrawerItem {
		override val name = "Settings"
	}

	data class RememberedCombat(val combatLink: CombatLink) : DrawerItem {
		override val name: String = combatLink.run {
			val stringBuilder = StringBuilder()
			if (isHost) stringBuilder.append("HOST ")
			if (sessionId != null) {
				stringBuilder.append(sessionId)
				stringBuilder.append('\n')
			}
			stringBuilder.append(combatLink.backend.hostUrl)
			stringBuilder.toString()
		}

	}
}

sealed class ContentState(val drawerItem: DrawerItem) {

	data class HostCombat(val hostCombatViewModel: HostCombatViewModel) :
	// TASK ugly line, change if possible
		ContentState(
			if (hostCombatViewModel.isSharing) DrawerItem.RememberedCombat(hostCombatViewModel.combatLink!!)
			else DrawerItem.HostCombat
		)

	data class JoinCombat(val joinViewModel: JoinViewModel) :
		ContentState(if (joinViewModel.asHost) DrawerItem.JoinAsHost else DrawerItem.JoinCombat)

	data class ClientCombat(val clientCombatViewModel: ClientCombatViewModel) :
		ContentState(DrawerItem.RememberedCombat(clientCombatViewModel.combatLink))

	data class CharacterScreen(val characterListViewModel: CharacterListViewModel) : ContentState(DrawerItem.Characters)

	data class SettingsScreen(val settingsViewModel: SettingsViewModel) : ContentState(DrawerItem.Settings)
}
