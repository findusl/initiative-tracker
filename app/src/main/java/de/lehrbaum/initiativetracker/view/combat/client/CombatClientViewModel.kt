package de.lehrbaum.initiativetracker.view.combat.client

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import de.lehrbaum.initiativetracker.extensions.DelegatingViewModel
import de.lehrbaum.initiativetracker.networking.RemoteCombatController
import de.lehrbaum.initiativetracker.view.combat.CombatantViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class CombatClientViewModel(
	sessionId: Int
) : DelegatingViewModel<CombatClientViewModel.Delegate>() {

	private val remoteCombatController = RemoteCombatController(sessionId)

	private val combatantsFlow = remoteCombatController.remoteCombat
		.map { combat ->
			val activeIndex = combat.activeCombatantIndex
			combat.combatants.mapIndexed { index, combatant ->
				with(combatant) {
					CombatantViewModel(id, name, initiative, active = index == activeIndex, editMode = false)
				}
			}
		}
		.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

	val combatants = combatantsFlow.asLiveData()

	interface Delegate
}