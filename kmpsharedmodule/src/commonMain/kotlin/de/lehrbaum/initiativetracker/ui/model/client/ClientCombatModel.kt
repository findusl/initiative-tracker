package de.lehrbaum.initiativetracker.ui.model.client

import androidx.compose.runtime.Stable
import de.lehrbaum.initiativetracker.bl.ClientCombatState
import kotlinx.coroutines.flow.Flow

@Stable
interface ClientCombatModel {
	val combatState: Flow<ClientCombatState>

	val combatId: Int

	fun leaveCombat()
}