package de.lehrbaum.initiativetracker.ui.host

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import de.lehrbaum.initiativetracker.bl.CombatController
import de.lehrbaum.initiativetracker.dtos.CombatantModel
import de.lehrbaum.initiativetracker.ui.edit.EditCombatantViewModel
import de.lehrbaum.initiativetracker.ui.shared.CombatantViewModel
import de.lehrbaum.initiativetracker.ui.shared.SnackbarState
import de.lehrbaum.initiativetracker.ui.shared.toCombatantViewModel
import kotlinx.coroutines.flow.combine

abstract class HostCombatViewModelBase : HostCombatViewModel {

	protected var combatController: CombatController = CombatController()

	override val combatants = combatController.combatants
		.combine(combatController.activeCombatantIndex) { combatants, activeIndex ->
			combatants.mapIndexed { index, combatant ->
				combatant.toCombatantViewModel(
					appOwnerId = combatController.ownerId,
					active = index == activeIndex,
				)
			}
		}

	override val editCombatantViewModel = mutableStateOf<EditCombatantViewModel?>(null)

	override val assignDamageCombatant = mutableStateOf<CombatantViewModel?>(null)

	override val snackbarState = mutableStateOf<SnackbarState?>(null)

	override var combatStarted by mutableStateOf(false)

	private var mostRecentDeleted: CombatantModel? = null

	override fun onCombatantClicked(combatantViewModel: CombatantViewModel) {
		if (combatStarted && !combatantViewModel.disabled) {
			damageCombatant(combatantViewModel)
		} else {
			editCombatant(combatantViewModel)
		}
	}

	override fun onCombatantLongClicked(combatant: CombatantViewModel) {
		editCombatant(combatant)
	}

	override fun deleteCombatant(combatantViewModel: CombatantViewModel) {
		mostRecentDeleted = combatController.deleteCombatant(combatantViewModel.id)
		// TODO show dialog with undo option
	}

	override fun disableCombatant(combatantViewModel: CombatantViewModel) {
		combatController.disableCombatant(combatantViewModel.id)
	}

	override fun enableCombatant(combatantViewModel: CombatantViewModel) {
		combatController.enableCombatant(combatantViewModel.id)
	}

	override fun jumpToCombatant(combatantViewModel: CombatantViewModel) {
		combatController.jumpToCombatant(combatantViewModel.id)
	}

	override fun onDamageDialogSubmit(damage: Int) {
		assignDamageCombatant.value?.apply {
			if (currentHp != null) // should have never been shown if null
				combatController.damageCombatant(id, damage)
		}
		assignDamageCombatant.value = null
	}

	override fun onDamageDialogCancel() {
		assignDamageCombatant.value = null
	}

	override fun addNewCombatant() {
		val newCombatant = combatController.addCombatant()
		editCombatant(newCombatant.toCombatantViewModel(combatController.ownerId), firstEdit = true)
	}

	private fun editCombatant(combatantViewModel: CombatantViewModel, firstEdit: Boolean = false) {
		editCombatantViewModel.value = EditCombatantViewModel(
			combatantViewModel,
			firstEdit,
			onSave = {
				combatController.updateCombatant(it)
				editCombatantViewModel.value = null
			},
			onCancel = {
				if (firstEdit)
					combatController.deleteCombatant(combatantViewModel.id)
				editCombatantViewModel.value = null
			}
		)
	}

	private fun damageCombatant(combatantViewModel: CombatantViewModel) {
		if (combatantViewModel.currentHp != null) {
			assignDamageCombatant.value = combatantViewModel
		} else {
			snackbarState.value = SnackbarState.Text("Combatant has no current HP")
		}
	}

	override fun undoDelete() {
		mostRecentDeleted?.let {
			combatController.addCombatant(it.name, it.initiative)
		}
	}

	override fun startCombat() {
		combatStarted = true
	}

	override fun nextCombatant() {
		combatController.nextTurn()
	}

	override fun previousCombatant() {
		combatController.prevTurn()
	}
}