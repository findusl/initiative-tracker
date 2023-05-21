package de.lehrbaum.initiativetracker.ui.host

import de.lehrbaum.initiativetracker.bl.HostConnectionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

data class HostSharedCombatModelImpl(override val sessionId: Int, private val leaveScreen: () -> Unit) : HostCombatModel {

	override val hostConnectionState: Flow<HostConnectionState>
		get() = flow {
			this.emit(HostConnectionState.Connecting)
			delay(10)
			this.emit(HostConnectionState.Connected)
		}
			.distinctUntilChanged()
			.flowOn(Dispatchers.IO)

	override val isSharing = true

	override suspend fun onShareClicked() {
		throw IllegalStateException("It should not be possible")
	}

	override suspend fun closeSession() {
		// we are actively still hosting it. Whatever
		withContext(Dispatchers.IO) {
			delay(10)
		}
		leaveScreen()
	}
}