package de.lehrbaum.initiativetracker.ui.model.client

import androidx.compose.runtime.mutableStateOf
import de.lehrbaum.initiativetracker.bl.ClientCombatSession
import de.lehrbaum.initiativetracker.bl.data.CombatLink
import de.lehrbaum.initiativetracker.bl.data.CombatLinkRepository
import de.lehrbaum.initiativetracker.ui.model.shared.CombatantViewModel
import de.lehrbaum.initiativetracker.ui.model.shared.SnackbarState

data class ClientCombatModelImpl(override val sessionId: Int, private val leaveScreen: () -> Unit): ClientCombatModel {
	private val combatSession = ClientCombatSession(sessionId)

	override val combatState = combatSession.state

	override val snackbarState = mutableStateOf<SnackbarState?>(null)

	override fun onCombatantClicked(combatantViewModel: CombatantViewModel) {
		snackbarState.value = SnackbarState.Text("Combat press not implemented")
	}

	override fun onCombatantLongClicked(combatant: CombatantViewModel) {
		snackbarState.value = SnackbarState.Text("Combat long press not implemented")
	}

	override fun leaveCombat() {
		CombatLinkRepository.removeCombatLink(CombatLink(sessionId, isHost = false))
		leaveScreen()
		// theoretically by leaving the screen it should remove the flow from the Composition
		// thereby cancelling the collection which in turn should cancel the Websocket connection
		// which removes the client from the combat.
		// If this client added characters to the combat we might have to send a remove command first
	}
}
