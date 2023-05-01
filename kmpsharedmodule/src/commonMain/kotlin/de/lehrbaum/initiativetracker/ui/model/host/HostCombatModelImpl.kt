package de.lehrbaum.initiativetracker.ui.model.host

import androidx.compose.material.SnackbarDuration.Long
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import de.lehrbaum.initiativetracker.bl.CombatController
import de.lehrbaum.initiativetracker.bl.CombatantModel
import de.lehrbaum.initiativetracker.bl.ShareCombatController
import de.lehrbaum.initiativetracker.networking.BestiaryNetworkClient
import de.lehrbaum.initiativetracker.ui.model.CombatantViewModel
import de.lehrbaum.initiativetracker.ui.model.SnackbarState
import de.lehrbaum.initiativetracker.ui.model.SwipeResponse
import de.lehrbaum.initiativetracker.ui.model.edit.EditCombatantModel
import de.lehrbaum.initiativetracker.ui.model.edit.EditCombatantModelImpl
import de.lehrbaum.initiativetracker.ui.model.toCombatantViewModel
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException

private const val TAG = "HostCombatModelImpl"

class HostCombatModelImpl: HostCombatModel, ShareCombatController.Delegate, CoroutineScope {
    /* Same as used in Androidx ViewModel. This makes the model cancellable. */
    override val coroutineContext = SupervisorJob() + Dispatchers.Default

    private var combatController: CombatController = CombatController()
    override val editCombatantModel = mutableStateOf<EditCombatantModel?>(null)

    override val combatants: StateFlow<List<CombatantViewModel>>
        get() = combatController.combatants
            .combine(combatController.activeCombatantIndex) { combatants, activeIndex ->
                combatants.mapIndexed { index, combatant ->
                    combatant.toCombatantViewModel(index == activeIndex)
                }
            }
            .stateIn(this, SharingStarted.Eagerly, emptyList())

    override val assignDamageCombatant = mutableStateOf<CombatantViewModel?>(null)
    override val snackbarState = mutableStateOf<SnackbarState?>(null)

    private val bestiaryNetworkClient = BestiaryNetworkClient()

    val allMonsterNames = bestiaryNetworkClient.monsters
        .map { monsters -> monsters.map { it.name }.toTypedArray() }
        .flowOn(Dispatchers.IO)
        // immediately start fetching as it takes a while
        .stateIn(this, SharingStarted.Eagerly, arrayOf())

    override var combatStarted by mutableStateOf(false)

    private val shareCombatController = ShareCombatController(combatController, this)

    private var mostRecentDeleted: CombatantModel? = null

    private var sharingCombatJob: Job? = null

    override var isSharing by mutableStateOf(false)

    init {
        observeSessionId()
        repeat(20) {
            combatController.addCombatant()
        }
    }

    private fun observeSessionId() {
        this.launch {
            shareCombatController.sessionId.collect { sessionId ->
                if (sessionId != null) {
                    showSessionId()
                }
            }
        }
    }

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

    override fun onCombatantSwipedToEnd(combatantViewModel: CombatantViewModel): SwipeResponse {
        snackbarState.value = SnackbarState.Text("Slide not yet implemented", Long)
        return SwipeResponse.SLIDE_BACK
    }

    override fun onCombatantSwipedToStart(combatantViewModel: CombatantViewModel): SwipeResponse {
        mostRecentDeleted = combatController.deleteCombatant(combatantViewModel.id)
        // TODO notify deleted. Make extra delete function
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

    fun previousCombatant() {
        combatController.prevTurn()
    }

    override fun onShareClicked() {
        if (isSharing) {
            snackbarState.value =
                SnackbarState.Text("Stop your current share to start a new one.")
            return
        }

        sharingCombatJob = launch {
            isSharing = true
            handleWebsocketErrors {
                shareCombatController.shareCombatState()
            }
        }
        sharingCombatJob?.invokeOnCompletion { isSharing = false }
    }

    private suspend fun handleWebsocketErrors(block: suspend () -> Unit) {
        try {
            block()
        } catch (e: CancellationException) {
            Napier.i("Job cancelled", tag = TAG)
            snackbarState.value = SnackbarState.Text("Connection closed")
            throw e
        } catch (e: SocketTimeoutException) {
            Napier.w("Socket timeout", e, TAG)
            snackbarState.value = SnackbarState.Text("Connection failed")
        } catch (e: ClosedReceiveChannelException) {
            Napier.w("Channel closed", e, TAG)
            snackbarState.value = SnackbarState.Text("Connection closed")
        } catch (e: Exception) {
            Napier.e("Exception during sharing", e, TAG)
            val message = e.message ?: "No message: ${e::class}"
            snackbarState.value = SnackbarState.Text("Exception: $message")
        }
    }

    override fun onStopShareClicked() {
        if (isSharing) {
            sharingCombatJob?.cancel()
        }
    }

    override fun showSessionId() {
        val sessionId = shareCombatController.sessionId.value ?: -1
        snackbarState.value = SnackbarState.Copyable(
            "SessionId $sessionId",
            copyText = sessionId.toString()
        )
    }

    override suspend fun handleAddExternalCombatant(combatantModel: CombatantModel) {
        TODO("Not yet implemented")
    }
}