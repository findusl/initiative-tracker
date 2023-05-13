package de.lehrbaum.initiativetracker.ui.model.client

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import de.lehrbaum.initiativetracker.bl.ClientCombatState
import de.lehrbaum.initiativetracker.ui.model.CombatantViewModel
import de.lehrbaum.initiativetracker.ui.model.SnackbarState
import kotlinx.coroutines.flow.Flow

@Stable
interface ClientCombatModel {
	val combatState: Flow<ClientCombatState>
	val snackbarState: MutableState<SnackbarState?>
	val sessionId: Int

	fun onCombatantClicked(combatantViewModel: CombatantViewModel)
	fun onCombatantLongClicked(combatant: CombatantViewModel)
	fun leaveCombat()
}