package de.lehrbaum.initiativetracker.ui.host

import de.lehrbaum.initiativetracker.bl.DamageDecision
import de.lehrbaum.initiativetracker.dtos.CombatantModel

class HostLocalCombatViewModelImpl: HostCombatViewModel() {
	override val confirmDamage = null
	override val showAutoConfirmDamageToggle = false
	override val autoConfirmDamage = false
	override val isSharing = false
	override val title = "Combat"

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
		throw IllegalStateException("It should not be possible")
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
