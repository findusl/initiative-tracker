package de.lehrbaum.initiativetracker.ui.host

import de.lehrbaum.initiativetracker.GlobalInstances
import de.lehrbaum.initiativetracker.bl.HostConnectionState
import de.lehrbaum.initiativetracker.bl.data.CombatLink
import de.lehrbaum.initiativetracker.ui.composables.DamageOption
import kotlinx.coroutines.flow.flowOf

data class HostLocalCombatViewModelImpl(private val navigateToSharedCombat: (CombatLink) -> Unit): HostCombatViewModelBase() {
	override val hostConnectionState = flowOf(HostConnectionState.Connected)
	override val confirmDamage = null
	override val isSharing = false
	override val combatLink = null
	override val title = "New Combat"

	override fun onConfirmDamageDialogSubmit(option: DamageOption) {
		throw IllegalStateException("It should not be possible")
	}

	override fun onConfirmDamageDialogCancel() {
		throw IllegalStateException("It should not be possible")
	}

	override suspend fun shareCombat() {
		GlobalInstances.backendNetworkClient
			.createSession(combatController.combatants.value, combatController.activeCombatantIndex.value, TODO())
			//.getOrNullAndHandle("Unable to create combat on Server.")
			//?.let { navigateToSharedCombat(it) }
	}

	override suspend fun closeSession() {
		throw IllegalStateException("It should not be possible")
	}

	override fun showSessionId() {
		throw IllegalStateException("It should not be possible")
	}
}