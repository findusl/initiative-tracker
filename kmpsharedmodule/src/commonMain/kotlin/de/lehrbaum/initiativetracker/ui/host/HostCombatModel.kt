package de.lehrbaum.initiativetracker.ui.host

import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import de.lehrbaum.initiativetracker.bl.HostConnectionState
import de.lehrbaum.initiativetracker.ui.edit.EditCombatantModel
import kotlinx.coroutines.flow.Flow

@Stable
interface HostCombatModel {
	val hostConnectionState: Flow<HostConnectionState>
	val editCombatantModel: State<EditCombatantModel?>
    val isSharing: Boolean
	val sessionId: Int

    suspend fun onShareClicked()
	suspend fun closeSession()
}
