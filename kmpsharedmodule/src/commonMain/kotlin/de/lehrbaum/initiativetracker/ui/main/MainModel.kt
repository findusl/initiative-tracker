package de.lehrbaum.initiativetracker.ui.main

import de.lehrbaum.initiativetracker.ui.host.HostCombatModel


sealed interface ContentState {
	data class HostCombat(val hostCombatModel: HostCombatModel): ContentState
}
