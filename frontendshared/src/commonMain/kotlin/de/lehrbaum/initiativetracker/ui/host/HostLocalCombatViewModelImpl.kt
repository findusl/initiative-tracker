package de.lehrbaum.initiativetracker.ui.host

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import de.lehrbaum.initiativetracker.GlobalInstances
import de.lehrbaum.initiativetracker.bl.HostConnectionState
import de.lehrbaum.initiativetracker.bl.data.Backend
import de.lehrbaum.initiativetracker.bl.data.CombatLink
import de.lehrbaum.initiativetracker.ui.composables.DamageOption
import kotlinx.coroutines.flow.flowOf

data class HostLocalCombatViewModelImpl(private val navigateToSharedCombat: (CombatLink) -> Unit): HostCombatViewModelBase() {
	override val hostConnectionState = flowOf(HostConnectionState.Connected)
	override val confirmDamage = null
	override val isSharing = false
	override val combatLink = null
	override val title = "New Combat"

	override var backendInputViewModel: BackendInputViewModel? by mutableStateOf(null)
		private set

	override fun onConfirmDamageDialogSubmit(option: DamageOption) {
		throw IllegalStateException("It should not be possible")
	}

	override fun onConfirmDamageDialogCancel() {
		throw IllegalStateException("It should not be possible")
	}

	override suspend fun shareCombat() {
		backendInputViewModel = BackendInputViewModel(
			onBackendConfirmed = {
				if (shareCombat(it))
					backendInputViewModel = null
			},
			onDismiss = { backendInputViewModel = null }
		)
	}

	private suspend fun shareCombat(backend: Backend): Boolean {
		val sessionId = GlobalInstances.backendNetworkClient
			.createSession(combatController.combatants.value, combatController.activeCombatantIndex.value, backend)
			.getOrNullAndHandle("Unable to create combat on Server.")
			?.also { navigateToSharedCombat(CombatLink(backend, isHost = true, sessionId = it)) }
		return sessionId != null
	}

	override suspend fun closeSession() {
		throw IllegalStateException("It should not be possible")
	}

	override fun showSessionId() {
		throw IllegalStateException("It should not be possible")
	}
}