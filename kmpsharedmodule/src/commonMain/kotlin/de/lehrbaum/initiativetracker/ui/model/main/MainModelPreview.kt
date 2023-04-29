package de.lehrbaum.initiativetracker.ui.model.main

import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.flow.MutableStateFlow

class MainModelPreview: MainModel {
	override val drawerItems = MutableStateFlow(listOf(
		DrawerItem.JoinCombat(active = true),
		DrawerItem.HostCombat(active = false)
	))
	override val content = mutableStateOf(ContentState.Empty)
}
