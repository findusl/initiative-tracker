package de.lehrbaum.initiativetracker.ui.host

import de.lehrbaum.initiativetracker.GlobalInstances
import de.lehrbaum.initiativetracker.bl.HostConnectionState
import de.lehrbaum.initiativetracker.ui.composables.DamageOption
import de.lehrbaum.initiativetracker.ui.shared.CombatantViewModel
import kotlinx.coroutines.flow.flowOf

data class HostLocalCombatViewModelImpl(private val navigateToSharedCombat: (Int) -> Unit): HostCombatViewModelBase() {
	override val hostConnectionState = flowOf(HostConnectionState.Connected)
	override val confirmDamage: Pair<Int, CombatantViewModel>? = null
	override val isSharing = false
	override val sessionId = -1

	override fun onConfirmDamageDialogSubmit(option: DamageOption) {
		throw IllegalStateException("It should not be possible")
	}

	override fun onConfirmDamageDialogCancel() {
		throw IllegalStateException("It should not be possible")
	}

	override suspend fun shareCombat() {
		val sessionId = GlobalInstances.backendApi
			.createSession(combatController.combatants.value, combatController.activeCombatantIndex.value)
		navigateToSharedCombat(sessionId)
	}

	override suspend fun closeSession() {
		throw IllegalStateException("It should not be possible")
	}

	override fun showSessionId() {
		throw IllegalStateException("It should not be possible")
	}
}