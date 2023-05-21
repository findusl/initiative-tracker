package de.lehrbaum.initiativetracker.ui

sealed interface HostConnectionState {
	object Connecting: HostConnectionState
	object Connected: HostConnectionState
}