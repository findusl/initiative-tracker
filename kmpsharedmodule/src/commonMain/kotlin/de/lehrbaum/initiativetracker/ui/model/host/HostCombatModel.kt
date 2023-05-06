package de.lehrbaum.initiativetracker.ui.model.host

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import de.lehrbaum.initiativetracker.ui.model.CombatantViewModel
import de.lehrbaum.initiativetracker.ui.model.SnackbarState
import de.lehrbaum.initiativetracker.ui.model.SwipeResponse
import de.lehrbaum.initiativetracker.ui.model.edit.EditCombatantModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlin.coroutines.CoroutineContext

@Stable
interface HostCombatModel: CoroutineScope {
    val editCombatantModel: State<EditCombatantModel?>
    val combatants: StateFlow<List<CombatantViewModel>>
    val assignDamageCombatant: MutableState<CombatantViewModel?>
    val snackbarState: MutableState<SnackbarState?>
    val combatStarted: Boolean
    val isSharing: Boolean

    fun onCombatantPressed(combatantViewModel: CombatantViewModel)
    fun onCombatantLongPressed(combatant: CombatantViewModel)
    fun onCombatantSwipedToEnd(combatantViewModel: CombatantViewModel): SwipeResponse
    fun onCombatantSwipedToStart(combatantViewModel: CombatantViewModel): SwipeResponse
    fun onDamageDialogSubmit(damage: Int)
    fun onAddNewPressed()
    fun nextCombatant()
    fun undoDelete()
    fun startCombat()
    fun onShareClicked()
    fun onStopShareClicked()
    fun showSessionId()
}
