package de.lehrbaum.initiativetracker.ui.host

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import de.lehrbaum.initiativetracker.GlobalInstances
import de.lehrbaum.initiativetracker.bl.DamageDecision
import de.lehrbaum.initiativetracker.data.BackendUri
import de.lehrbaum.initiativetracker.data.CombatLink
import de.lehrbaum.initiativetracker.dtos.CombatantModel
import de.lehrbaum.initiativetracker.networking.hosting.HostConnectionState
import kotlinx.coroutines.flow.flowOf

data class HostLocalCombatViewModelImpl(private val navigateToSharedCombat: (CombatLink) -> Unit): HostCombatViewModel() {
	override val hostConnectionState = flowOf(HostConnectionState.Connected)
	override val confirmDamage = null
	override val showAutoConfirmDamageToggle = false
	override val autoConfirmDamage = false
	override val isSharing = false
	override val title = "New Combat"

	override var backendInputViewModel: BackendInputViewModel? by mutableStateOf(null)
		private set

	override fun onConfirmDamageDialogSubmit(decision: DamageDecision) {
		throw IllegalStateException("It should not be possible")
	}

	override fun autoConfirmDamagePressed() {
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

	private suspend fun shareCombat(backendUri: BackendUri): Boolean {
		val sessionId = GlobalInstances.backendNetworkClient
			.createSession(combatController.combatants.value, combatController.activeCombatantIndex.value, backendUri)
			.getOrNullAndHandle("Unable to create combat on LocalHostServer.")
			?.also { navigateToSharedCombat(CombatLink(backendUri, isHost = true, sessionId = it)) }
		return sessionId != null
	}

	override suspend fun closeSession() {
		throw IllegalStateException("It should not be possible")
	}

	override fun showSessionId() {
		throw IllegalStateException("It should not be possible")
	}

	override suspend fun confirmDamage(damage: Int, target: CombatantModel, probableSource: String?): DamageDecision? {
		throw IllegalStateException("It should not be possible")
	}
}