package de.lehrbaum.initiativetracker.ui.model.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import de.lehrbaum.initiativetracker.ui.model.host.HostCombatModel
import de.lehrbaum.initiativetracker.ui.model.host.HostCombatModelImpl
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.concurrent.CancellationException

class MainModelImpl: MainModel {
	override var activeDrawerItem by mutableStateOf<DrawerItem>(DrawerItem.HostCombat)

	override val drawerItems = MutableStateFlow(listOf(
        DrawerItem.JoinCombat,
        DrawerItem.HostCombat,
        DrawerItem.HostExistingCombat,
        DrawerItem.Characters
    ))

	/** Keep a default hostCombatState to return to */
	private val hostCombatState = hostNewCombat()

    override var content by mutableStateOf<ContentState>(hostCombatState)

    override fun onDrawerItemSelected(item: DrawerItem) {
        val newContent: ContentState? = when (item) {
            is DrawerItem.Characters -> null
            is DrawerItem.HostCombat -> hostNewCombat()
            is DrawerItem.HostExistingCombat -> TODO()
            is DrawerItem.JoinCombat -> joinCombat()
            is DrawerItem.RememberedCombat -> TODO()
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
		TODO("Implement")
	}

	private fun ContentState.cancelContent() {
		// TODO implement once some contents are cancellable
	}
}