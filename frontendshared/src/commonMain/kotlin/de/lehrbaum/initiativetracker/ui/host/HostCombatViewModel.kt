package de.lehrbaum.initiativetracker.ui.host

import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import de.lehrbaum.initiativetracker.GlobalInstances
import de.lehrbaum.initiativetracker.bl.AOEDecision
import de.lehrbaum.initiativetracker.bl.AudioCommandController
import de.lehrbaum.initiativetracker.bl.CombatCommand
import de.lehrbaum.initiativetracker.bl.CombatController
import de.lehrbaum.initiativetracker.bl.ConfirmationRequester
import de.lehrbaum.initiativetracker.bl.DamageDecision
import de.lehrbaum.initiativetracker.bl.PreliminaryAOEResult
import de.lehrbaum.initiativetracker.bl.model.AoeOptions
import de.lehrbaum.initiativetracker.dtos.CombatantModel
import de.lehrbaum.initiativetracker.ui.character.CharacterChooserViewModel
import de.lehrbaum.initiativetracker.ui.composables.CombatantListViewModel
import de.lehrbaum.initiativetracker.ui.damage.DamageCombatantViewModel
import de.lehrbaum.initiativetracker.ui.edit.EditCombatantViewModel
import de.lehrbaum.initiativetracker.ui.shared.ErrorStateHolder
import de.lehrbaum.initiativetracker.ui.shared.ErrorStateHolder.Impl
import de.lehrbaum.initiativetracker.ui.shared.SnackbarState
import de.lehrbaum.initiativetracker.ui.shared.showText
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.job
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

@Stable
abstract class HostCombatViewModel : ErrorStateHolder by Impl(), ConfirmationRequester {

	@Suppress("LeakingThis") // TASK migrate to the sharedHostCombatViewModel, only necessary there
	protected var combatController: CombatController =
		CombatController(GlobalInstances.generalSettingsRepository, this)

	val combatants = combatController.combatants
		.combine(combatController.activeCombatantIndex) { combatants, activeIndex ->
			combatants.mapIndexed { index, combatant ->
				CombatantListViewModel(
					combatant,
					active = index == activeIndex,
					isOwned = combatant.ownerId == combatController.hostId
				)
			}.toImmutableList()
		}

	val editCombatantViewModel = mutableStateOf<EditCombatantViewModel?>(null)

	var damageCombatantViewModel by mutableStateOf<DamageCombatantViewModel?>(null)
		private set

	var characterChooserViewModel by mutableStateOf<CharacterChooserViewModel?>(null)
		private set

	val snackbarState = mutableStateOf<SnackbarState?>(null)

	var combatStarted by mutableStateOf(false)

	private var mostRecentDeleted: CombatantModel? = null

	abstract val title: String
	abstract val isSharing: Boolean
	abstract val confirmDamage: ConfirmDamageOptions?
	abstract val showAutoConfirmDamageToggle: Boolean
	abstract val autoConfirmDamage: Boolean

	private val audioCommandController = AudioCommandController(combatController)
	val isRecordActionVisible: Boolean
		get() = audioCommandController.isAvailable
	var isRecording by mutableStateOf(false)
		private set
	private var processingRecordingJob by mutableStateOf<Job?>(null)
	val isProcessingRecording by derivedStateOf { processingRecordingJob != null }

	private var resetConfirmationContinuation: Continuation<Boolean>? = null
	var showResetConfirmation by mutableStateOf(false)
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
		coroutineScope {
			processingRecordingJob = this.coroutineContext.job
			isRecording = false
			val commandResult = audioCommandController.processRecording()
			commandResult.getOrNullAndHandle()?.let { command ->
				when (command) {
					is CombatCommand.DamageCommand -> {
						damageCombatant(command.target, command.damage)
					}
				}
			}

			processingRecordingJob = null
		}
	}

	fun cancelRecording() {
		audioCommandController.cancelRecording()
		isRecording = false
		processingRecordingJob?.cancel()
		processingRecordingJob = null
	}

	fun onCombatantClicked(combatantModel: CombatantModel) {
		if (combatStarted && !combatantModel.disabled) {
			damageCombatant(combatantModel)
		} else {
			editCombatant(combatantModel)
		}
	}

	fun onCombatantLongClicked(combatant: CombatantModel) {
		editCombatant(combatant)
	}

	fun deleteCombatant(combatantModel: CombatantModel) {
		mostRecentDeleted = combatController.deleteCombatant(combatantModel.id)
		// TASK show dialog with undo option
	}

	fun disableCombatant(combatantModel: CombatantModel) {
		combatController.disableCombatant(combatantModel.id)
	}

	fun enableCombatant(combatantModel: CombatantModel) {
		combatController.enableCombatant(combatantModel.id)
	}

	fun jumpToCombatant(combatantModel: CombatantModel) {
		combatController.jumpToCombatant(combatantModel.id)
	}

	fun addNewCombatant() {
		val newCombatant = combatController.addCombatant()
		editCombatant(newCombatant, firstEdit = true)
	}

	fun addExistingCharacter() {
		characterChooserViewModel = CharacterChooserViewModel(
			onChosen = { character, initiative, currentHp ->
				val combatant = combatController.addCombatant(character.name, initiative)
				combatController.updateCombatant(combatant.copy(currentHp = currentHp, maxHp = character.maxHp))
				characterChooserViewModel = null
			},
			onCancel = {
				characterChooserViewModel = null
			}
		)
	}

	private fun editCombatant(combatantModel: CombatantModel, firstEdit: Boolean = false) {
		editCombatantViewModel.value = EditCombatantViewModel(
			combatantModel,
			firstEdit,
			onSave = {
				combatController.updateCombatant(it)
				editCombatantViewModel.value = null
			},
			onCancel = {
				if (firstEdit)
					combatController.deleteCombatant(combatantModel.id)
				editCombatantViewModel.value = null
			}
		)
	}

	private fun damageCombatant(combatantModel: CombatantModel, initialDamage: Int = 1) {
		if (combatantModel.currentHp != null) {
			damageCombatantViewModel = DamageCombatantViewModel(
				combatantModel.name,
				onSubmit = { damage ->
					combatController.damageCombatant(combatantModel.id, damage)
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

	// Even shown for host aoe damage, so not only shared model
	override suspend fun confirmAoe(
		aoeOptions: AoeOptions,
		targetRolls: Map<CombatantModel, PreliminaryAOEResult>,
		probableSource: String?
	): Map<CombatantModel, AOEDecision>? {
		TODO("Not yet implemented")
	}

	abstract suspend fun closeSession()
	abstract fun showSessionId()
	abstract suspend fun shareCombat()
	abstract fun onConfirmDamageDialogCancel()
	abstract fun onConfirmDamageDialogSubmit(decision: DamageDecision)
	abstract fun autoConfirmDamagePressed()

	fun onResetResponse(response: Boolean) {
		resetConfirmationContinuation?.resume(response)
		showResetConfirmation = false
		resetConfirmationContinuation = null
	}

	suspend fun resetCombat() {
		val confirmation = suspendCancellableCoroutine { continuation ->
			resetConfirmationContinuation = continuation
			showResetConfirmation = true
			// TASK this style is also used for the confirm damage dialog and others.
			// It can probably be extracted to a more generic variant
			continuation.invokeOnCancellation {
				showResetConfirmation = false
				resetConfirmationContinuation = null
			}
		}
		if (confirmation) {
			combatController.reset()
			combatStarted = false
		}
	}
}
