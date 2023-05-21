package de.lehrbaum.initiativetracker.ui.host

import androidx.compose.runtime.mutableStateOf
import de.lehrbaum.initiativetracker.bl.CombatController
import de.lehrbaum.initiativetracker.ui.edit.EditCombatantModel


abstract class HostCombatModelBase : HostCombatModel {

	protected var combatController: CombatController = CombatController()

	override val editCombatantModel = mutableStateOf<EditCombatantModel?>(null)
}