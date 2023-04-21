package de.lehrbaum.initiativetracker.view.combat.client

import androidx.lifecycle.viewModelScope
import de.lehrbaum.initiativetracker.extensions.DelegatingViewModel
import de.lehrbaum.initiativetracker.networking.RemoteCombatController
import de.lehrbaum.initiativetracker.view.combat.CombatantViewModel
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@Suppress("unused")
private const val TAG = "CombatClientViewModel"

class CombatClientViewModelImpl(
	sessionId: Int
) : DelegatingViewModel<CombatClientViewModelImpl.Delegate>(), ClientCombatViewModel {

	private val remoteCombatController = RemoteCombatController(sessionId)

	override val combatants = remoteCombatController.remoteCombat
		.catch {
			Napier.e("Caught an exception from remoteCombat", it, TAG)
			delegate?.leaveCombat()
		}
		.map { combat ->
			val activeIndex = combat.activeCombatantIndex
			combat.combatants.mapIndexed { index, combatant ->
				with(combatant) {
					CombatantViewModel(id, name, initiative, maxHp, currentHp, active = index == activeIndex)
				}
			}
		}
		.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

	interface Delegate {
		fun leaveCombat()
	}
}