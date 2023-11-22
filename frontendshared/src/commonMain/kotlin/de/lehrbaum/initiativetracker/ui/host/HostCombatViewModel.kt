package de.lehrbaum.initiativetracker.ui.host

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import de.lehrbaum.initiativetracker.GlobalInstances
import de.lehrbaum.initiativetracker.bl.*
import de.lehrbaum.initiativetracker.dtos.CombatantModel
import de.lehrbaum.initiativetracker.ui.damage.DamageCombatantViewModel
import de.lehrbaum.initiativetracker.ui.edit.EditCombatantViewModel
import de.lehrbaum.initiativetracker.ui.shared.*
import de.lehrbaum.initiativetracker.ui.shared.ErrorStateHolder.Impl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

abstract class HostCombatViewModel: ErrorStateHolder by Impl(), ConfirmationRequester {

	@Suppress("LeakingThis") // TASK migrate to the sharedHostCombatViewModel, only necessary there
	protected var combatController: CombatController =
		CombatController(GlobalInstances.generalSettingsRepository, this)

	val combatants = combatController.combatants
		.combine(combatController.activeCombatantIndex) { combatants, activeIndex ->
			combatants.mapIndexed { index, combatant ->
				combatant.toCombatantViewModel(
					thisUser = combatController.hostId,
					active = index == activeIndex,
				)
			}
		}

	val editCombatantViewModel = mutableStateOf<EditCombatantViewModel?>(null)

	var damageCombatantViewModel by mutableStateOf<DamageCombatantViewModel?>(null)
		private set

	val snackbarState = mutableStateOf<SnackbarState?>(null)

	var combatStarted by mutableStateOf(false)

	private var mostRecentDeleted: CombatantModel? = null

	open val backendInputViewModel: BackendInputViewModel? = null

	abstract val title: String
	abstract val hostConnectionState: Flow<HostConnectionState>
	abstract val isSharing: Boolean
	abstract val confirmDamage: ConfirmDamageOptions?

	private val audioCommandController = AudioCommandController(combatController)
	val isRecordActionVisible: Boolean
		get() = audioCommandController.isAvailable
	var isRecording by mutableStateOf(false)
		private set
	@Suppress("MemberVisibilityCanBePrivate") // TODO use
	var isProcessingRecording by mutableStateOf(false)
		private set

	fun recordCommand() {
		if (isRecording) return
		if (!audioCommandController.isAvailable) {
			snackbarState.showText("Audio recording unavailable")
		} else {
			isRecording = true
			audioCommandController.startRecordingCommand()
		}
	}

	suspend fun finishRecording() {
		if (isProcessingRecording) return
		isProcessingRecording = true
		isRecording = false
		val commandResult = audioCommandController.finishRecordingCommand()
		commandResult.getOrNullAndHandle()?.let { command ->
			when (command) {
				is CombatCommand.DamageCommand -> {
					damageCombatant(command.target.toCombatantViewModel(combatController.hostId), command.damage)
				}
			}
		}

		isProcessingRecording = false
	}

	fun cancelRecording() {
		audioCommandController.cancelRecording()
		isRecording = false
	}

	fun onCombatantClicked(combatantViewModel: CombatantViewModel) {
		if (combatStarted && !combatantViewModel.disabled) {
			damageCombatant(combatantViewModel)
		} else {
			editCombatant(combatantViewModel)
		}
	}

	fun onCombatantLongClicked(combatant: CombatantViewModel) {
		editCombatant(combatant)
	}

	fun deleteCombatant(combatantViewModel: CombatantViewModel) {
		mostRecentDeleted = combatController.deleteCombatant(combatantViewModel.id)
		// TASK show dialog with undo option
	}

	fun disableCombatant(combatantViewModel: CombatantViewModel) {
		combatController.disableCombatant(combatantViewModel.id)
	}

	fun enableCombatant(combatantViewModel: CombatantViewModel) {
		combatController.enableCombatant(combatantViewModel.id)
	}

	fun jumpToCombatant(combatantViewModel: CombatantViewModel) {
		combatController.jumpToCombatant(combatantViewModel.id)
	}

	fun addNewCombatant() {
		val newCombatant = combatController.addCombatant()
		editCombatant(newCombatant.toCombatantViewModel(combatController.hostId), firstEdit = true)
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

	private fun damageCombatant(combatantViewModel: CombatantViewModel, initialDamage: Int = 1) {
		if (combatantViewModel.currentHp != null) {
			damageCombatantViewModel = DamageCombatantViewModel(
				combatantViewModel.name,
				onSubmit = { damage ->
					combatController.damageCombatant(combatantViewModel.id, damage)
					damageCombatantViewModel = null
				},
				onCancel = { damageCombatantViewModel = null },
				initialDamage,
			)
		} else {
			snackbarState.value = SnackbarState.Text("Combatant has no current HP")
		}
	}

	@Suppress("unused") // TASK implement undo
	fun undoDelete() {
		mostRecentDeleted?.let {
			combatController.addCombatant(it.name, it.initiative)
		}
	}

	fun startCombat() {
		combatStarted = true
	}

	fun nextCombatant() {
		combatController.nextTurn()
	}

	abstract suspend fun closeSession()
	abstract fun showSessionId()
	abstract suspend fun shareCombat()
	abstract fun onConfirmDamageDialogCancel()
	abstract fun onConfirmDamageDialogSubmit(decision: DamageDecision)
}