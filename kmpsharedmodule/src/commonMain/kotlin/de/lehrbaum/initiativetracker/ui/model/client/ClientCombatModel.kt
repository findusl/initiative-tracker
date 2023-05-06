package de.lehrbaum.initiativetracker.ui.model.client

import de.lehrbaum.initiativetracker.ui.model.CombatantViewModel
import kotlinx.coroutines.flow.StateFlow

interface ClientCombatModel {
	val combatants: StateFlow<List<CombatantViewModel>>

	fun leaveCombat()
}