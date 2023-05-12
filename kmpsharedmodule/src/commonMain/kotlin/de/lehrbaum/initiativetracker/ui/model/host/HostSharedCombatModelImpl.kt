package de.lehrbaum.initiativetracker.ui.model.host

import androidx.compose.material.SnackbarDuration
import de.lehrbaum.initiativetracker.bl.HostCombatSession
import de.lehrbaum.initiativetracker.bl.HostConnectionState
import de.lehrbaum.initiativetracker.ui.model.SnackbarState
import kotlinx.coroutines.flow.Flow

class HostSharedCombatModelImpl(override val sessionId: Int) : HostCombatModelBase() {
	private val hostCombatSession = HostCombatSession(sessionId, combatController)
	override val hostConnectionState: Flow<HostConnectionState>
		get() = hostCombatSession.hostConnectionState
	override val isSharing = true

	override fun onShareClicked() {
		throw IllegalStateException("It should not be possible")
	}

	override fun closeSession() {
		TODO("Not yet implemented")
		// Should be some separate command
	}

	override fun showSessionId() {
		snackbarState.value = SnackbarState.Copyable("SessionId: $sessionId", SnackbarDuration.Long, sessionId.toString())
	}
}