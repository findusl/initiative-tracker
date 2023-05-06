package de.lehrbaum.initiativetracker.view.combat.edit

import de.lehrbaum.initiativetracker.bl.model.CombatantModel
import de.lehrbaum.initiativetracker.extensions.DelegatingViewModel

class EditCombatantViewModel : DelegatingViewModel<EditCombatantViewModel.Delegate>() {


	interface Delegate {
		fun combatantSaved(combatantModel: CombatantModel)
	}
}