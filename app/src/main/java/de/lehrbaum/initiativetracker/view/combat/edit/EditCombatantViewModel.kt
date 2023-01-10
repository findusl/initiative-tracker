package de.lehrbaum.initiativetracker.view.combat.edit

import de.lehrbaum.initiativetracker.extensions.DelegatingViewModel
import de.lehrbaum.initiativetracker.logic.CombatantModel

class EditCombatantViewModel : DelegatingViewModel<EditCombatantViewModel.Delegate>() {


	interface Delegate {
		fun combatantSaved(combatantModel: CombatantModel)
	}
}