package de.lehrbaum.initiativetracker

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import de.lehrbaum.initiativetracker.bestiary.BestiaryNetworkClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

class InitiativeViewModel : DelegatingViewModel<InitiativeViewModel.Delegate>() {
	private val editingCombatantId = MutableStateFlow<Long?>(null)

	private val currentCombatController = CombatController()

	private val combatantsFlow = combine(
		currentCombatController.currentCombat, currentCombatController.activeCombatantIndex, editingCombatantId
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
		.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

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
		val oldSelectedCombatantViewModel = combatantsFlow.value.first { it.id == currentlyEditingCombatant.id }
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
		val updatedCombatant = updatedCombatantViewModel.run { CombatantModel(id, name, initiative) }
		currentCombatController.updateCombatant(updatedCombatant)
	}

	fun nextTurn() = currentCombatController.nextTurn()

	fun prevTurn() = currentCombatController.prevTurn()

	interface Delegate {
		fun showSaveChangesDialog(onOkListener: () -> Unit)
	}
}
