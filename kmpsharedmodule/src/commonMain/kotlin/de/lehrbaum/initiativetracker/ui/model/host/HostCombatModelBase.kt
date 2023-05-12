package de.lehrbaum.initiativetracker.ui.model.host

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import de.lehrbaum.initiativetracker.bl.CombatController
import de.lehrbaum.initiativetracker.bl.model.CombatantModel
import de.lehrbaum.initiativetracker.ui.model.CombatantViewModel
import de.lehrbaum.initiativetracker.ui.model.SnackbarState
import de.lehrbaum.initiativetracker.ui.model.edit.EditCombatantModel
import de.lehrbaum.initiativetracker.ui.model.edit.EditCombatantModelImpl
import de.lehrbaum.initiativetracker.ui.model.toCombatantViewModel
import kotlinx.coroutines.flow.combine


abstract class HostCombatModelBase : HostCombatModel {

	protected var combatController: CombatController = CombatController()

	override val combatants = combatController.combatants
		.combine(combatController.activeCombatantIndex) { combatants, activeIndex ->
			combatants.mapIndexed { index, combatant ->
				combatant.toCombatantViewModel(index == activeIndex)
			}
		}

	override val editCombatantModel = mutableStateOf<EditCombatantModel?>(null)

	override val assignDamageCombatant = mutableStateOf<CombatantViewModel?>(null)

	override val snackbarState = mutableStateOf<SnackbarState?>(null)

	override var combatStarted by mutableStateOf(false)

	private var mostRecentDeleted: CombatantModel? = null

	override fun onCombatantPressed(combatantViewModel: CombatantViewModel) {
		if (combatStarted) {
			damageCombatant(combatantViewModel)
		} else {
			editCombatant(combatantViewModel)
		}
	}

	override fun onCombatantLongPressed(combatant: CombatantViewModel) {
		editCombatant(combatant)
	}

	override fun deleteCombatant(combatantViewModel: CombatantViewModel) {
		mostRecentDeleted = combatController.deleteCombatant(combatantViewModel.id)
		// TODO show dialog with undo option
	}

	override fun onDamageDialogSubmit(damage: Int) {
		assignDamageCombatant.value?.apply {
			combatController.updateCombatant(copy(currentHp = currentHp - damage).toCombatantModel())
		}
		assignDamageCombatant.value = null
	}

	override fun onAddNewPressed() {
		val newCombatant = combatController.addCombatant()
		editCombatant(newCombatant.toCombatantViewModel())
	}

	private fun editCombatant(combatantViewModel: CombatantViewModel) {
		editCombatantModel.value = EditCombatantModelImpl(
			combatantViewModel,
			onSave = {
				combatController.updateCombatant(it)
				editCombatantModel.value = null
			},
			onCancel = { editCombatantModel.value = null }
		)
	}

	private fun damageCombatant(CombatantViewModel: CombatantViewModel) {
		assignDamageCombatant.value = CombatantViewModel
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