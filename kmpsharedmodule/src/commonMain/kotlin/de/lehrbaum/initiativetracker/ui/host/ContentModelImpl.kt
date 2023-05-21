package de.lehrbaum.initiativetracker.ui.host

import de.lehrbaum.initiativetracker.bl.HostConnectionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlin.random.Random

data class ContentModelImpl(
	override val id: Int,
	private val navigateToDifferentId: (Int) -> Unit
) : ContentModel {

	override val hostConnectionState: Flow<HostConnectionState>
		get() = flow {
			this.emit(HostConnectionState.Connecting)
			delay(10)
			this.emit(HostConnectionState.Connected)
		}
			.distinctUntilChanged()
			.flowOn(Dispatchers.IO)

	override suspend fun onShareClicked() {
		delay(10)
		navigateToDifferentId(Random.nextInt(10000))
	}
}