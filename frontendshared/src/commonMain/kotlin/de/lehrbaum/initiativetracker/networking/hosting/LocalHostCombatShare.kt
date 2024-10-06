package de.lehrbaum.initiativetracker.networking.hosting

import de.lehrbaum.initiativetracker.bl.CombatController
import de.lehrbaum.initiativetracker.bl.HostCombatShare
import de.lehrbaum.initiativetracker.bl.HostEventHandler
import de.lehrbaum.initiativetracker.data.CombatLink
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LocalHostCombatShare(
	private val combatLink: CombatLink,
	private val combatController: CombatController,
) : HostCombatShare {

	private val hostEventHandler = HostEventHandler(combatController)

	override val hostConnectionState: Flow<HostConnectionState> = flow {
		try {

		} finally {
			// TODO remove from server
		}
		TODO()
	}

}
