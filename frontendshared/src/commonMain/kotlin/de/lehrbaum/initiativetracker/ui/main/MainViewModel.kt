package de.lehrbaum.initiativetracker.ui.main

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import de.lehrbaum.initiativetracker.GlobalInstances
import de.lehrbaum.initiativetracker.bl.MonsterCache
import de.lehrbaum.initiativetracker.networking.bestiary.MonsterDTO
import de.lehrbaum.initiativetracker.ui.character.CharacterListViewModel
import de.lehrbaum.initiativetracker.ui.host.HostLocalCombatViewModelImpl
import de.lehrbaum.initiativetracker.ui.settings.SettingsViewModel
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Stable
open class MainViewModel {

	/* Persistent ViewModels */
	private val localCombatContentState = hostNewCombat()

	var content by mutableStateOf<ContentState>(localCombatContentState)
	private val backstack = mutableStateListOf<ContentState>()
	val canStepBack: Boolean
		get() = backstack.isNotEmpty() // since this is backed by a state variable the ui is notified

	private var cacheInitialized: Boolean = false

	fun onDrawerItemSelected(item: DrawerItem) {
		if (item == content.drawerItem) return // avoid double click race conditions
		val newContent: ContentState = when (item) {
			is DrawerItem.Characters -> ContentState.CharacterScreen(CharacterListViewModel())
			is DrawerItem.Combat -> localCombatContentState
			DrawerItem.Settings -> ContentState.SettingsScreen(SettingsViewModel())
		}
		backstack.add(index = 0, content)
		content = newContent
	}

	fun initializeCache(scope: CoroutineScope) {
		if (cacheInitialized) return
		Napier.i("Initializing Cache")
		scope.launch {
			GlobalInstances.bestiaryNetworkClient.monsters.collect {
				MonsterCache.monsters = it
			}
			MonsterCache.monstersByName = MonsterCache.monsters.associateBy(MonsterDTO::displayName)
			cacheInitialized = true
			Napier.i("Cache Initialized")
		}
	}

	fun onBackPressed() {
		content = backstack.removeFirstOrNull() ?: return
	}

	private fun hostNewCombat(): ContentState.HostLocalCombat {
		val hostCombatModel = HostLocalCombatViewModelImpl()
		return ContentState.HostLocalCombat(hostCombatModel)
	}
}

sealed interface DrawerItem {
	val name: String

	data object Combat : DrawerItem {
		override val name = "Combat"
	}

	data object Characters : DrawerItem {
		override val name = "Characters"
	}

	data object Settings : DrawerItem {
		override val name = "Settings"
	}
}

sealed class ContentState(val drawerItem: DrawerItem) {

	data class HostLocalCombat(val hostCombatViewModel: HostLocalCombatViewModelImpl) :
		ContentState(DrawerItem.Combat)

	data class CharacterScreen(val characterListViewModel: CharacterListViewModel) : ContentState(DrawerItem.Characters)

	data class SettingsScreen(val settingsViewModel: SettingsViewModel) : ContentState(DrawerItem.Settings)
}
