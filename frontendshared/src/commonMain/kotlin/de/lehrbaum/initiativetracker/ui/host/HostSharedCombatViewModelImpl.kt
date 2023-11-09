package de.lehrbaum.initiativetracker.ui.host

import androidx.compose.material.SnackbarDuration
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import de.lehrbaum.initiativetracker.GlobalInstances
import de.lehrbaum.initiativetracker.bl.HostCombatSession
import de.lehrbaum.initiativetracker.bl.HostConnectionState
import de.lehrbaum.initiativetracker.bl.data.CombatLink
import de.lehrbaum.initiativetracker.bl.data.CombatLinkRepository
import de.lehrbaum.initiativetracker.dtos.commands.ServerToHostCommand
import de.lehrbaum.initiativetracker.ui.composables.ConfirmDamageOptions
import de.lehrbaum.initiativetracker.ui.composables.DamageOption
import de.lehrbaum.initiativetracker.ui.composables.DamageOption.*
import de.lehrbaum.initiativetracker.ui.shared.SnackbarState
import de.lehrbaum.initiativetracker.ui.shared.toCombatantViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

data class HostSharedCombatViewModelImpl(
	override val combatLink: CombatLink,
	private val leaveScreen: () -> Unit
) : HostCombatViewModelBase(), HostCombatSession.Delegate {
	private val hostCombatSession = HostCombatSession(combatLink, combatController, this)
	override val hostConnectionState: Flow<HostConnectionState>
		get() = hostCombatSession.hostConnectionState
	override var confirmDamage: ConfirmDamageOptions? by mutableStateOf(null)
		private set
	private var confirmDamageContinuation: Continuation<Boolean>? = null
	override val isSharing = true
	override val title = "Hosting ${combatLink.userDescription}"

	override fun onConfirmDamageDialogCancel() {
		confirmDamageContinuation?.resume(false)
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
			"SessionId: ${combatLink.sessionId ?: "default"}",
			SnackbarDuration.Long,
			combatLink.sessionId.toString()
		)
	}

	override suspend fun handleDamageCombatantCommand(command: ServerToHostCommand.DamageCombatant): Boolean {
		val combatant = combatController.combatants.value.first { it.id == command.combatantId }.toCombatantViewModel(combatController.ownerId)
		if (combatant.ownerId == command.ownerId) {
			combatController.damageCombatant(combatant.id, command.damage)
			return true
		}
		// I don't have a name of the player, so I take the first combatant they control that is not a creature
		// I just hope that's their main character
		val probableSource = combatController.combatants.value
			.firstOrNull { it.ownerId == command.ownerId && it.creatureType == null }
		return suspendCancellableCoroutine {
			confirmDamageContinuation = it
			confirmDamage = ConfirmDamageOptions(command.damage, combatant, probableSource?.name)
		}
	}

	override fun onConfirmDamageDialogSubmit(option: DamageOption) {
		val (damage, combatant) = confirmDamage ?: return
		val actualDamage = when (option) {
			FULL -> damage
			HALF -> damage / 2
			DOUBLE -> damage * 2
			NONE -> 0
		}
		if (actualDamage > 0)
			combatController.damageCombatant(combatant.id, actualDamage)
		confirmDamageContinuation?.resume(true)
		confirmDamage = null
	}
}