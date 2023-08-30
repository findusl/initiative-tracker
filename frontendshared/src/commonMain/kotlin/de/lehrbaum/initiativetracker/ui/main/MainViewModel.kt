package de.lehrbaum.initiativetracker.ui.main

import androidx.compose.runtime.Stable
import de.lehrbaum.initiativetracker.networking.bestiary.MonsterDTO
import de.lehrbaum.initiativetracker.ui.character.CharacterListViewModel
import de.lehrbaum.initiativetracker.ui.client.ClientCombatViewModel
import de.lehrbaum.initiativetracker.ui.host.HostCombatViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

@Stable // assumes content is backed by mutableState variable. Hope I don't forget
interface MainViewModel {
	val drawerItems: Flow<List<DrawerItem>>
	val content: ContentState
	val canStepBack: Boolean

	fun onDrawerItemSelected(item: DrawerItem)

	fun initializeCache(scope: CoroutineScope)

	fun onBackPressed()

	object Cache {
		val monsters: MutableStateFlow<List<MonsterDTO>> = MutableStateFlow(emptyList())
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
	open val keepScreenOn: Boolean
		get() = false

	data class HostCombat(val hostCombatViewModel: HostCombatViewModel) :
	// TODO ugly line, consider changing
		ContentState(if (hostCombatViewModel.isSharing) DrawerItem.RememberedCombat(hostCombatViewModel.sessionId, isHost = true) else DrawerItem.HostCombat) {
		override val keepScreenOn: Boolean
			get() = hostCombatViewModel.isSharing
	}

	data class JoinCombat(val onJoin: (Int) -> Unit, val onCancel: () -> Unit, val asHost: Boolean) : ContentState(DrawerItem.JoinCombat)

	data class ClientCombat(val clientCombatViewModel: ClientCombatViewModel) :
		ContentState(DrawerItem.RememberedCombat(clientCombatViewModel.sessionId, isHost = false)) {
		override val keepScreenOn: Boolean
			get() = true
	}

	data class CharacterScreen(val characterListViewModel: CharacterListViewModel) : ContentState(DrawerItem.Characters)
}
