package de.lehrbaum.initiativetracker.ui.host

import androidx.compose.material.SnackbarDuration
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import de.lehrbaum.initiativetracker.GlobalInstances
import de.lehrbaum.initiativetracker.bl.DamageDecision
import de.lehrbaum.initiativetracker.bl.HostCombatSession
import de.lehrbaum.initiativetracker.bl.HostConnectionState
import de.lehrbaum.initiativetracker.bl.data.CombatLink
import de.lehrbaum.initiativetracker.bl.data.CombatLinkRepository
import de.lehrbaum.initiativetracker.dtos.CombatantModel
import de.lehrbaum.initiativetracker.ui.shared.SnackbarState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

data class HostSharedCombatViewModelImpl(
	val combatLink: CombatLink,
	private val leaveScreen: () -> Unit
) : HostCombatViewModel() {
	private val hostCombatSession = HostCombatSession(combatLink, combatController)
	override val hostConnectionState: Flow<HostConnectionState>
		get() = hostCombatSession.hostConnectionState
	override var confirmDamage: ConfirmDamageOptions? by mutableStateOf(null)
		private set
	private var confirmDamageContinuation: Continuation<DamageDecision?>? = null
	override val isSharing = true
	override val title = "Hosting ${combatLink.userDescription}"

	override fun onConfirmDamageDialogCancel() {
		confirmDamageContinuation?.resume(null)
		confirmDamage = null
	}

	override suspend fun shareCombat() {
		throw IllegalStateException("It should not be possible")
	}

	override suspend fun closeSession() {
		// failure is hard to handle, since we want it to be gone... This will not actually show as we leave the screen
		GlobalInstances.backendNetworkClient.deleteSession(combatLink)
			.getOrNullAndHandle("Unable to delete combat on server")
		CombatLinkRepository.removeCombatLink(combatLink)
		leaveScreen()
	}

	override fun showSessionId() {
		if (combatLink.sessionId == null) return
		snackbarState.value = SnackbarState.Copyable(
			"SessionId: ${combatLink.sessionId}",
			SnackbarDuration.Long,
			combatLink.sessionId.toString()
		)
	}

	override fun onConfirmDamageDialogSubmit(decision: DamageDecision) {
		confirmDamageContinuation?.resume(decision)
		confirmDamage = null
	}

	override suspend fun confirmDamage(damage: Int, target: CombatantModel, probableSource: String?): DamageDecision? {
		return suspendCancellableCoroutine {
			confirmDamageContinuation = it
			confirmDamage = ConfirmDamageOptions(damage, target.name, probableSource)
		}
	}
}