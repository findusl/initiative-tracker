package de.lehrbaum.initiativetracker.bl

import de.lehrbaum.initiativetracker.networking.hosting.HostConnectionState
import kotlinx.coroutines.flow.Flow

interface HostCombatShare {
	val hostConnectionState: Flow<HostConnectionState>
}
