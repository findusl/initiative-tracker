package de.lehrbaum.initiativetracker.ui.model.main

import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import de.lehrbaum.initiativetracker.ui.model.client.ClientCombatModel
import de.lehrbaum.initiativetracker.ui.model.host.HostCombatModel
import kotlinx.coroutines.flow.StateFlow

@Stable // assumes content is backed by mutableState variable. Hope I don't forget
interface MainModel {
	val activeDrawerItem: DrawerItem
	val drawerItems: StateFlow<List<DrawerItem>>
	val content: ContentState

	fun onDrawerItemSelected(item: DrawerItem) { TODO() }
}

sealed interface DrawerItem {
	val name: String
	object JoinCombat: DrawerItem {
		override val name = "Join Combat"
	}
	object HostCombat: DrawerItem {
		override val name = "Host Combat"
	}
	object HostExistingCombat: DrawerItem {
		override val name = "Host existing Combat"
	}
	object Characters: DrawerItem {
		override val name = "Characters"
	}
	data class RememberedCombat(
		val id: Int,
		override val name: String
	): DrawerItem
}

sealed interface ContentState {
	object Empty: ContentState
	data class HostCombat(val hostCombatModel: HostCombatModel): ContentState
	data class JoinCombat(val onJoin: (Int) -> Unit, val onCancel: () -> Unit): ContentState
	data class ClientCombat(val clientCombatModel: ClientCombatModel): ContentState
	data class CharacterScreen(val tbd: Int) : ContentState
}
