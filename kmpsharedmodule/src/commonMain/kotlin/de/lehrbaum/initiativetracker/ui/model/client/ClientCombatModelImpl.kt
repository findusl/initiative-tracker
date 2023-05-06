package de.lehrbaum.initiativetracker.ui.model.client

import de.lehrbaum.initiativetracker.bl.ClientCombatSession
import de.lehrbaum.initiativetracker.bl.CombatLink
import de.lehrbaum.initiativetracker.bl.CombatLinkRepository

class ClientCombatModelImpl(override val combatId: Int, private val leaveScreen: () -> Unit): ClientCombatModel {
	private val combatSession = ClientCombatSession(combatId)

	override val combatState = combatSession.state

	override fun leaveCombat() {
		CombatLinkRepository.removeCombatLink(CombatLink(combatId, isHost = false))
		leaveScreen()
		// theoretically by leaving the screen it should remove the flow from the Composition
		// thereby cancelling the collection which in turn should cancel the Websocket connection
		// which removes the client from the combat.
		// If this client added characters to the combat we might have to send a remove command first
	}
}
