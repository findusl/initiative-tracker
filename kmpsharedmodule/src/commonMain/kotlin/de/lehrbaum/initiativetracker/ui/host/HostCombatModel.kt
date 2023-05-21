package de.lehrbaum.initiativetracker.ui.host

import androidx.compose.runtime.Stable
import de.lehrbaum.initiativetracker.bl.HostConnectionState
import kotlinx.coroutines.flow.Flow

@Stable
interface HostCombatModel {
	val hostConnectionState: Flow<HostConnectionState>
    val isSharing: Boolean
	val sessionId: Int

    suspend fun onShareClicked()
	suspend fun closeSession()
}
