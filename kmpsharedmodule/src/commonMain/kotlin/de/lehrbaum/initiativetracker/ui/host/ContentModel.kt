package de.lehrbaum.initiativetracker.ui.host

import androidx.compose.runtime.Stable
import de.lehrbaum.initiativetracker.bl.HostConnectionState
import kotlinx.coroutines.flow.Flow

@Stable
interface ContentModel {
	val hostConnectionState: Flow<HostConnectionState>
	val id: Int

    suspend fun onShareClicked()
}
