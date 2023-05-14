package de.lehrbaum.initiativetracker.ui.model.host

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import de.lehrbaum.initiativetracker.bl.HostConnectionState
import de.lehrbaum.initiativetracker.ui.model.edit.EditCombatantModel
import de.lehrbaum.initiativetracker.ui.model.shared.CombatantViewModel
import de.lehrbaum.initiativetracker.ui.model.shared.SnackbarState
import kotlinx.coroutines.flow.Flow

@Stable
interface HostCombatModel {
	val hostConnectionState: Flow<HostConnectionState>
    val combatants: Flow<List<CombatantViewModel>>
	val editCombatantModel: State<EditCombatantModel?>
    val assignDamageCombatant: MutableState<CombatantViewModel?>
    val snackbarState: MutableState<SnackbarState?>
    val combatStarted: Boolean
    val isSharing: Boolean
	val sessionId: Int

    fun onCombatantClicked(combatantViewModel: CombatantViewModel)
    fun onCombatantLongClicked(combatant: CombatantViewModel)
	fun deleteCombatant(combatantViewModel: CombatantViewModel)
    fun onDamageDialogSubmit(damage: Int)
    fun onAddNewPressed()
    fun nextCombatant()
    fun previousCombatant()
    fun undoDelete()
    fun startCombat()
    suspend fun onShareClicked()
	suspend fun closeSession()
	fun showSessionId()
}
