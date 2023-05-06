package de.lehrbaum.initiativetracker.bl

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class CombatSession(val sessionId: Int, asHost: Boolean): CoroutineScope {
	override val coroutineContext = SupervisorJob() + Dispatchers.IO

}
