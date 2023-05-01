package de.lehrbaum.initiativetracker.ui.model.main

import androidx.compose.runtime.mutableStateOf
import de.lehrbaum.initiativetracker.ui.model.host.HostCombatModel
import de.lehrbaum.initiativetracker.ui.model.host.HostCombatModelImpl
import kotlinx.coroutines.flow.MutableStateFlow

class MainModelImpl: MainModel {
    override val drawerItems = MutableStateFlow(listOf(
        DrawerItem.JoinCombat(),
        DrawerItem.HostCombat(active = true),
        DrawerItem.HostExistingCombat(),
        DrawerItem.Characters()
    ))

    override val content = mutableStateOf(hostNewCombat())

    override fun onDrawerItemSelected(item: DrawerItem) {
        when (item) {
            is DrawerItem.Characters -> TODO()
            is DrawerItem.HostCombat -> TODO()
            is DrawerItem.HostExistingCombat -> TODO()
            is DrawerItem.JoinCombat -> TODO()
            is DrawerItem.RememberedCombat -> TODO()
        }
    }

    private fun hostNewCombat(): ContentState.HostCombat {
        return ContentState.HostCombat(HostCombatModelImpl())
        // TODO cancel model once replaced
    }
}