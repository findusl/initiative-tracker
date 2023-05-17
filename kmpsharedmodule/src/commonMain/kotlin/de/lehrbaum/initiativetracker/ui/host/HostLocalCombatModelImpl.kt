package de.lehrbaum.initiativetracker.ui.host

import de.lehrbaum.initiativetracker.GlobalInstances
import de.lehrbaum.initiativetracker.bl.HostConnectionState
import kotlinx.coroutines.flow.flowOf

data class HostLocalCombatModelImpl(private val navigateToSharedCombat: (Int) -> Unit): HostCombatModelBase() {
	override val hostConnectionState = flowOf(HostConnectionState.Connected)
	override val isSharing = false
	override val sessionId = -1

	override suspend fun onShareClicked() {
		val sessionId = GlobalInstances.backendApi
			.createSession(combatController.combatants.value, combatController.activeCombatantIndex.value)
		navigateToSharedCombat(sessionId)
	}

	override suspend fun closeSession() {
		throw IllegalStateException("It should not be possible")
	}

	override fun showSessionId() {
		throw IllegalStateException("It should not be possible")
	}
}