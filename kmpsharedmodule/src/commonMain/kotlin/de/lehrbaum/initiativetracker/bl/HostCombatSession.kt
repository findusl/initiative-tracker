package de.lehrbaum.initiativetracker.bl

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class HostCombatSession() {
	val hostConnectionState = flow {
		emit(HostConnectionState.Connecting)
		delay(10)
		emit(HostConnectionState.Connected)
	}
		.distinctUntilChanged()
		.flowOn(Dispatchers.IO)
}

sealed interface HostConnectionState {
	object Connecting: HostConnectionState
	object Connected: HostConnectionState
}