package de.lehrbaum.initiativetracker.ui.model.host

import de.lehrbaum.initiativetracker.bl.HostConnectionState
import kotlinx.coroutines.flow.flowOf

class HostLocalCombatModelImpl: HostCombatModelBase() {
	override val hostConnectionState = flowOf(HostConnectionState.Connected)
	override val isSharing = false
	override val combatId = -1

	override fun onShareClicked() {
		TODO("Not yet implemented")
	}

	override fun closeSession() {
		throw IllegalStateException("It should not be possible")
	}

	override fun showSessionId() {
		throw IllegalStateException("It should not be possible")
	}
}