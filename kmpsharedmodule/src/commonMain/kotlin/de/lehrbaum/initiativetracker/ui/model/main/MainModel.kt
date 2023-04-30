package de.lehrbaum.initiativetracker.ui.model.main

import androidx.compose.runtime.State
import kotlinx.coroutines.flow.StateFlow

interface MainModel {
	val drawerItems: StateFlow<List<DrawerItem>>
	val content: State<ContentState>

	fun onDrawerItemSelected(item: DrawerItem) { TODO() }
}

sealed interface DrawerItem {
	val name: String
	val active: Boolean
	data class JoinCombat(override val active: Boolean): DrawerItem {
		override val name = "Join Combat"
	}
	data class HostCombat(override val active: Boolean): DrawerItem {
		override val name = "Host Combat"
	}
	data class HostExistingCombat(override val active: Boolean): DrawerItem {
		override val name = "Host existing Combat"
	}
	data class Characters(override val active: Boolean): DrawerItem {
		override val name = "Characters"
	}
	data class RememberedCombat(val id: Int, override val active: Boolean, override val name: String): DrawerItem
}

sealed interface ContentState {
	object Empty: ContentState
	data class CharacterScreen(val tbd: Int) : ContentState
}
