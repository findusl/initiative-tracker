package de.lehrbaum.initiativetracker.ui.main

import androidx.compose.runtime.Stable
import de.lehrbaum.initiativetracker.ui.character.CharacterListModel
import de.lehrbaum.initiativetracker.ui.client.ClientCombatModel
import de.lehrbaum.initiativetracker.ui.host.HostCombatModel
import kotlinx.coroutines.flow.Flow

@Stable // assumes content is backed by mutableState variable. Hope I don't forget
interface MainModel {
	val activeDrawerItem: DrawerItem
	val drawerItems: Flow<List<DrawerItem>>
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
		val isHost: Boolean,
		override val name: String = id.toString() + " Host".takeIf { isHost }
	): DrawerItem
}

sealed interface ContentState {
	object Empty: ContentState
	data class HostCombat(val hostCombatModel: HostCombatModel): ContentState
	data class JoinCombat(val onJoin: (Int) -> Unit, val onCancel: () -> Unit, val asHost: Boolean): ContentState
	data class ClientCombat(val clientCombatModel: ClientCombatModel): ContentState
	data class CharacterScreen(val characterListModel: CharacterListModel) : ContentState
}
