package de.lehrbaum.initiativetracker.view.combat.host

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import de.lehrbaum.initiativetracker.extensions.DelegatingViewModel
import de.lehrbaum.initiativetracker.logic.CombatController
import de.lehrbaum.initiativetracker.logic.CombatantModel
import de.lehrbaum.initiativetracker.networking.BestiaryNetworkClient
import de.lehrbaum.initiativetracker.networking.ShareCombatController
import de.lehrbaum.initiativetracker.view.combat.CombatantViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CombatHostViewModel : DelegatingViewModel<CombatHostViewModel.Delegate>() {
	private val editingCombatantId = MutableStateFlow<Long?>(null)

	private val currentCombatController = CombatController()

	private val shareCombatController = ShareCombatController(currentCombatController)

	private var mostRecentDeleted: CombatantModel? = null

	private val combatantsFlow = combine(
		currentCombatController.combatants, currentCombatController.activeCombatantIndex, editingCombatantId
	) { combatants, activeCombatantIndex, editingCombatantId ->
		combatants.mapIndexed { index, combatant ->
			val isActive = activeCombatantIndex == index
			val isInEditMode = combatant.id == editingCombatantId
			CombatantViewModel(
				combatant.id,
				combatant.name,
				combatant.initiative,
				isInEditMode,
				isActive,
			)
		}.toList()
	}
		.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

	val combatants = combatantsFlow.asLiveData()

	private val bestiaryNetworkClient = BestiaryNetworkClient()

	val allMonsterNamesLiveData = bestiaryNetworkClient.monsters
		.map { monsters -> monsters.map { it.name }.toTypedArray() }
		.flowOn(Dispatchers.IO)
		// immediately start fetching as it takes a while
		.stateIn(viewModelScope, SharingStarted.Eagerly, arrayOf())
		.asLiveData()

	/**
	 * Set by the adapter. Should probably hide behind functions but easier for now.
	 */
	var currentlyEditingCombatant: CombatantViewModel? = null

	val isSharing: Boolean
		get() = shareCombatController.isSharing

	init {
		addCombatant()
		addCombatant()
	}

	fun selectCombatant(combatantToSelect: CombatantViewModel?) {
		if (combatantToSelect?.editMode == true) return // nothing to do
		checkIfShouldSave()
		// could block so that the user still sees his changes in the background. Use coroutines. Future improvements
		editingCombatantId.value = combatantToSelect?.id
	}

	private fun checkIfShouldSave() {
		val currentlyEditingCombatant = currentlyEditingCombatant ?: return
		val oldSelectedCombatantViewModel = combatantsFlow.value.firstOrNull { it.id == currentlyEditingCombatant.id } ?: return
		val sanitizedEditingCombatant = currentlyEditingCombatant
			.copy(active = oldSelectedCombatantViewModel.active, editMode = oldSelectedCombatantViewModel.editMode)
		if (sanitizedEditingCombatant != oldSelectedCombatantViewModel) {
			delegate?.showSaveChangesDialog {
				updateCombatant(sanitizedEditingCombatant.copy(editMode = false))
			}
		}
	}

	fun addCombatant() = currentCombatController.addCombatant()

	fun updateCombatant(updatedCombatantViewModel: CombatantViewModel) {
		val updatedCombatant = with(updatedCombatantViewModel) { CombatantModel(id, name, initiative) }
		currentCombatController.updateCombatant(updatedCombatant)
	}

	fun nextTurn() = currentCombatController.nextTurn()

	fun prevTurn() = currentCombatController.prevTurn()

	fun deleteCombatant(position: Int) {
		mostRecentDeleted = currentCombatController.deleteCombatant(position)
	}

	fun undoDelete() {
		mostRecentDeleted?.let {
			currentCombatController.addCombatant(it.name, it.initiative)
		}
	}

	fun onShareClicked() {
		if (!shareCombatController.isSharing) {
			viewModelScope.launch {
				val sessionId = shareCombatController.startSharing(viewModelScope)
				delegate?.showSessionId(sessionId)
			}
		}
	}

	fun onStopShareClicked() {
		if (shareCombatController.isSharing) {
			shareCombatController.stopSharing()
		}
	}

	fun showSessionId() {
		val sessionId = shareCombatController.sessionId ?: -1
		delegate?.showSessionId(sessionId)
	}

	interface Delegate {
		fun showSaveChangesDialog(onOkListener: () -> Unit)
		fun showSessionId(sessionCode: Int)
	}
}
