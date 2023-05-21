package de.lehrbaum.initiativetracker.ui.main

import androidx.compose.runtime.Stable
import de.lehrbaum.initiativetracker.ui.host.HostCombatModel

@Stable // assumes content is backed by mutableState variable. Hope I don't forget
interface MainModel {
	val content: ContentState
}

sealed interface ContentState {
	data class HostCombat(val hostCombatModel: HostCombatModel): ContentState
}
