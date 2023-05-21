package de.lehrbaum.initiativetracker.ui.host

import de.lehrbaum.initiativetracker.GlobalInstances
import de.lehrbaum.initiativetracker.bl.HostCombatSession
import de.lehrbaum.initiativetracker.bl.HostConnectionState
import kotlinx.coroutines.flow.Flow

data class HostSharedCombatModelImpl(override val sessionId: Int, private val leaveScreen: () -> Unit) : HostCombatModelBase() {
	private val hostCombatSession = HostCombatSession()
	override val hostConnectionState: Flow<HostConnectionState>
		get() = hostCombatSession.hostConnectionState
	override val isSharing = true

	override suspend fun onShareClicked() {
		throw IllegalStateException("It should not be possible")
	}

	override suspend fun closeSession() {
		// we are actively still hosting it. Whatever
		GlobalInstances.backendApi.deleteSession()
		leaveScreen()
	}
}