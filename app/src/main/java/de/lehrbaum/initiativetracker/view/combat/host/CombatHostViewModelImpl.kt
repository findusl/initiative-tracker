package de.lehrbaum.initiativetracker.view.combat.host

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import de.lehrbaum.initiativetracker.bl.CombatController
import de.lehrbaum.initiativetracker.bl.CombatantModel
import de.lehrbaum.initiativetracker.extensions.DelegatingViewModel
import de.lehrbaum.initiativetracker.networking.BestiaryNetworkClient
import de.lehrbaum.initiativetracker.bl.ShareCombatController
import de.lehrbaum.initiativetracker.ui.model.CombatantViewModel
import de.lehrbaum.initiativetracker.ui.model.toCombatantViewModel
import de.lehrbaum.initiativetracker.ui.model.SwipeResponse
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException

@Suppress("unused")
private const val TAG = "HostCombatViewModelImpl"

class CombatHostViewModelImpl :
	DelegatingViewModel<CombatHostViewModelImpl.Delegate>(),
	ShareCombatController.Delegate,
	HostCombatViewModel {

	private var combatController: CombatController = CombatController()

	override val combatants: StateFlow<List<CombatantViewModel>>
		get() = combatController.combatants
			.combine(combatController.activeCombatantIndex) { combatants, activeIndex ->
				combatants.mapIndexed { index, combatant ->
					combatant.toCombatantViewModel(index == activeIndex)
				}
			}
			.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

	private val _hostEditCombatantViewModel = mutableStateOf<HostEditCombatantViewModel?>(null)
	override val hostEditCombatantViewModel: State<HostEditCombatantViewModel?>
		get() = _hostEditCombatantViewModel
	override val assignDamageCombatant = mutableStateOf<CombatantViewModel?>(null)


	private val bestiaryNetworkClient = BestiaryNetworkClient()

	val allMonsterNamesLiveData = bestiaryNetworkClient.monsters
		.map { monsters -> monsters.map { it.name }.toTypedArray() }
		.flowOn(Dispatchers.IO)
		// immediately start fetching as it takes a while
		.stateIn(viewModelScope, SharingStarted.Eagerly, arrayOf())
		.asLiveData()

	override val combatStarted = MutableStateFlow(false)

	private val shareCombatController = ShareCombatController(combatController, this)

	private var mostRecentDeleted: CombatantModel? = null

	private var sharingCombatJob: Job? = null

	val isSharing: Boolean
		get() = sharingCombatJob?.isActive == true

	init {
		viewModelScope.launch {
			shareCombatController.sessionId.collect { sessionId ->
				if (sessionId != null) {
					delegate?.showSessionId(sessionId)
				}
			}
		}
		repeat(20) {
			combatController.addCombatant()
		}
	}

	override fun onCombatantPressed(combatantViewModel: CombatantViewModel) {
		if (combatStarted.value) {
			damageCombatant(combatantViewModel)
		} else {
			editCombatant(combatantViewModel)
		}
	}

	override fun onCombatantLongPressed(combatant: CombatantViewModel) {
		editCombatant(combatant)
	}

	override fun onCombatantSwipedToEnd(combatantViewModel: CombatantViewModel): SwipeResponse {
		delegate?.showErrorMessage("Slide not yet implemented")
		return SwipeResponse.SLIDE_BACK
	}

	override fun onCombatantSwipedToStart(combatantViewModel: CombatantViewModel): SwipeResponse {
		combatController.deleteCombatant(combatantViewModel.id)
		return SwipeResponse.SLIDE_OUT
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

	private fun editCombatant(CombatantViewModel: CombatantViewModel) {
		_hostEditCombatantViewModel.value = HostEditCombatantViewModelImpl(CombatantViewModel)
	}

	private fun damageCombatant(CombatantViewModel: CombatantViewModel) {
		assignDamageCombatant.value = CombatantViewModel
	}

	fun undoDelete() {
		mostRecentDeleted?.let {
			combatController.addCombatant(it.name, it.initiative)
		}
	}

	fun startCombat() {
		combatStarted.value = true
	}

	override fun nextCombatant() {
		combatController.nextTurn()
	}

	fun previousCombatant() {
		combatController.prevTurn()
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

	override suspend fun handleAddExternalCombatant(combatantModel: CombatantModel) {
		TODO("Not yet implemented")
	}

	inner class HostEditCombatantViewModelImpl(CombatantViewModel: CombatantViewModel) : HostEditCombatantViewModel {
		private val id = CombatantViewModel.id
		override val name = mutableStateOf(CombatantViewModel.name)
		override val nameError = mutableStateOf(false)
		override val initiativeString = mutableStateOf(CombatantViewModel.initiativeString)
		override val initiativeError = mutableStateOf(false)
		override val maxHpString = mutableStateOf(CombatantViewModel.maxHp.toString())
		override val maxHpError = mutableStateOf(false)
		override val currentHpString = mutableStateOf(CombatantViewModel.currentHp.toString())
		override val currentHpError = mutableStateOf(false)


		override fun onSavePressed() {
			val initiative = initiativeString.value.toIntOrNull()
			initiativeError.value = initiative == null
			val maxHp = maxHpString.value.toIntOrNull()
			maxHpError.value = maxHp == null
			val currentHp = currentHpString.value.toIntOrNull()
			currentHpError.value = currentHp == null
			val name = this.name.value
			nameError.value = name.isBlank()
			if (initiative != null && name.isNotBlank() && maxHp != null && currentHp != null) {
				combatController.updateCombatant(CombatantModel(id, name, initiative, maxHp, currentHp))
				_hostEditCombatantViewModel.value = null
			}
		}

		override fun onCancelPressed() {
			_hostEditCombatantViewModel.value = null
		}

	}

	interface Delegate {
		fun showSessionId(sessionCode: Int)
		fun notifyConnectionFailed()
		fun notifyAlreadySharing()
		fun notifySessionHasExistingHost()
		fun notifySessionNotFound(sessionId: Int)
		fun notifySessionClosed()
		fun notifyCombatantDeleted()
		suspend fun allowAddExternalCharacter(name: String): Boolean
		fun showErrorMessage(message: String) // for debugging
	}
}
