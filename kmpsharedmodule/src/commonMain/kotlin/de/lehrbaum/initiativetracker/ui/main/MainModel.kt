package de.lehrbaum.initiativetracker.ui.main

import androidx.compose.runtime.Stable
import de.lehrbaum.initiativetracker.ui.host.HostCombatModel
import kotlinx.coroutines.flow.Flow

@Stable // assumes content is backed by mutableState variable. Hope I don't forget
interface MainModel {
	val activeDrawerItem: DrawerItem
	val drawerItems: Flow<List<DrawerItem>>
	val content: ContentState

	fun onDrawerItemSelected(item: DrawerItem)
}

sealed interface DrawerItem {
	val name: String
	object OtherModel: DrawerItem {
		override val name = "Other Model"
	}
	object HostCombat: DrawerItem {
		override val name = "Host Combat"
	}
	data class RememberedCombat(
		val id: Int,
		val isHost: Boolean,
		override val name: String = id.toString() + if (isHost) " Host" else " Client"
	): DrawerItem
}

sealed interface ContentState {
	object Empty: ContentState
	data class HostCombat(val hostCombatModel: HostCombatModel): ContentState
}
