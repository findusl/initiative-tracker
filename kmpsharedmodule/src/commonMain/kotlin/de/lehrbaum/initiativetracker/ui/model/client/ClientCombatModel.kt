package de.lehrbaum.initiativetracker.ui.model.client

import de.lehrbaum.initiativetracker.ui.model.CombatantViewModel
import kotlinx.coroutines.flow.StateFlow
import java.io.Closeable

interface ClientCombatModel: Closeable {
	val combatants: StateFlow<List<CombatantViewModel>>

	fun leaveCombat()
}