package de.lehrbaum.initiativetracker.ui.host

import androidx.compose.material.SnackbarDuration
import de.lehrbaum.initiativetracker.GlobalInstances
import de.lehrbaum.initiativetracker.bl.HostCombatSession
import de.lehrbaum.initiativetracker.bl.HostConnectionState
import de.lehrbaum.initiativetracker.bl.data.CombatLink
import de.lehrbaum.initiativetracker.bl.data.CombatLinkRepository
import de.lehrbaum.initiativetracker.ui.shared.SnackbarState
import kotlinx.coroutines.flow.Flow

data class HostSharedCombatModelImpl(override val sessionId: Int, private val leaveScreen: () -> Unit) : HostCombatModelBase() {
	private val hostCombatSession = HostCombatSession(sessionId, combatController)
	override val hostConnectionState: Flow<HostConnectionState>
		get() = hostCombatSession.hostConnectionState
	override val isSharing = true

	override suspend fun shareCombat() {
		throw IllegalStateException("It should not be possible")
	}

	override suspend fun closeSession() {
		// we are actively still hosting it. Whatever
		GlobalInstances.backendApi.deleteSession(sessionId)
		CombatLinkRepository.removeCombatLink(CombatLink(sessionId, isHost = true))
		leaveScreen()
	}

	override fun showSessionId() {
		snackbarState.value = SnackbarState.Copyable("SessionId: $sessionId", SnackbarDuration.Long, sessionId.toString())
	}
}