package de.lehrbaum.initiativetracker.ui.model.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.flow.MutableStateFlow

class MainModelPreview: MainModel {
	override val activeDrawerItem = DrawerItem.HostCombat
	override val drawerItems = MutableStateFlow(listOf(
		DrawerItem.JoinCombat,
		DrawerItem.HostCombat
	))
	override val content by mutableStateOf<ContentState>(ContentState.Empty)
}
