package de.lehrbaum.initiativetracker.view.combat.host

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import de.lehrbaum.initiativetracker.extensions.DelegatingViewModel
import de.lehrbaum.initiativetracker.logic.CombatController
import de.lehrbaum.initiativetracker.logic.CombatantModel
import de.lehrbaum.initiativetracker.networking.BestiaryNetworkClient
import de.lehrbaum.initiativetracker.networking.ShareCombatController
import de.lehrbaum.initiativetracker.view.combat.CombatantViewModel
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException

@Suppress("unused")
private const val TAG = "CombatHostViewModel"

class CombatHostViewModel : DelegatingViewModel<CombatHostViewModel.Delegate>(), ShareCombatController.Delegate {
	private val editingCombatantId = MutableStateFlow<Long?>(null)

	private val currentCombatController = CombatController()

	private val shareCombatController = ShareCombatController(currentCombatController, this)

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
		}
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

	private var sharingCombatJob: Job? = null

	val isSharing: Boolean
		get() = sharingCombatJob?.isActive == true

	init {
		addCombatant()
		addCombatant()
		viewModelScope.launch {
			shareCombatController.sessionId.collect { sessionId ->
				if (sessionId != null) {
					delegate?.showSessionId(sessionId)
				}
			}
		}
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

	fun onJoinAsHostClicked(sessionId: Int) {
		if (isSharing) {
			delegate?.notifyAlreadySharing()
			return
		}

		sharingCombatJob = viewModelScope.launch {
			handleWebsocketErrors {
				val result = shareCombatController.joinCombatAsHost(sessionId)
				when (result) {
					ShareCombatController.Result.ALREADY_HOSTED -> delegate?.notifySessionHasExistingHost()
					ShareCombatController.Result.NOT_FOUND -> delegate?.notifySessionNotFound(sessionId)
					else -> {}
				}
			}
		}
	}

	fun onShareClicked() {
		if (isSharing) {
			delegate?.notifyAlreadySharing()
			return
		}

		sharingCombatJob = viewModelScope.launch {
			handleWebsocketErrors {
				shareCombatController.shareCombatState()
			}
		}
	}

	private suspend fun handleWebsocketErrors(block: suspend () -> Unit) {
		try {
			block()
		} catch (e: CancellationException) {
			Napier.i("Job cancelled", tag = TAG)
			delegate?.notifySessionClosed()
			throw e
		} catch (e: SocketTimeoutException) {
			Napier.w("Socket timeout", e, TAG)
			delegate?.notifyConnectionFailed()
		} catch (e: ClosedReceiveChannelException) {
			Napier.w("Channel closed", e, TAG)
			delegate?.notifySessionClosed()
		} catch (e: Exception) {
			Napier.e("Exception during sharing", e, TAG)
			delegate?.showErrorMessage(e.message ?: "No message: ${e::class}")
		}
	}

	fun onStopShareClicked() {
		if (isSharing) {
			sharingCombatJob?.cancel()
		}
	}

	fun showSessionId() {
		val sessionId = shareCombatController.sessionId.value ?: -1
		delegate?.showSessionId(sessionId)
	}

	override suspend fun addCombatant(combatantModel: CombatantModel) {
		if (delegate?.allowAddCharacter(combatantModel.name) == true) {
			currentCombatController.addCombatant(combatantModel.name, combatantModel.initiative)
		}
	}

	interface Delegate {
		fun showSaveChangesDialog(onOkListener: () -> Unit)
		fun showSessionId(sessionCode: Int)
		fun notifyConnectionFailed()
		fun notifyAlreadySharing()
		fun notifySessionHasExistingHost()
		fun notifySessionNotFound(sessionId: Int)
		fun notifySessionClosed()
		suspend fun allowAddCharacter(name: String): Boolean
		fun showErrorMessage(message: String) // for faster debugging
	}
}
