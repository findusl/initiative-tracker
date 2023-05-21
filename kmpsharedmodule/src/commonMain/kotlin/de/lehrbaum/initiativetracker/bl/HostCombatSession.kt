package de.lehrbaum.initiativetracker.bl

sealed interface HostConnectionState {
	object Connecting: HostConnectionState
	object Connected: HostConnectionState
}