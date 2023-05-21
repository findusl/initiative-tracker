package de.lehrbaum.initiativetracker.ui.host

import de.lehrbaum.initiativetracker.bl.HostConnectionState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlin.random.Random

data class HostLocalCombatModelImpl(private val navigateToSharedCombat: (Int) -> Unit): HostCombatModel {
	override val hostConnectionState = flowOf(HostConnectionState.Connected)
	override val isSharing = false
	override val sessionId = -1

	override suspend fun onShareClicked() {
		delay(10)
		navigateToSharedCombat(Random.nextInt(10000))
	}

	override suspend fun closeSession() {
		throw IllegalStateException("It should not be possible")
	}
}