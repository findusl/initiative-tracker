package de.lehrbaum.initiativetracker.ui.model.host

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import de.lehrbaum.initiativetracker.ui.model.CombatantViewModel
import de.lehrbaum.initiativetracker.ui.model.SnackbarState
import de.lehrbaum.initiativetracker.ui.model.SwipeResponse
import kotlinx.coroutines.flow.StateFlow

@Stable
interface HostCombatModel {
    val combatants: StateFlow<List<CombatantViewModel>>
    //val hostEditCombatantViewModel: State<HostEditCombatantViewModel?>
    val assignDamageCombatant: MutableState<CombatantViewModel?>
    val snackbarState: MutableState<SnackbarState?>
    val combatStarted: StateFlow<Boolean>

    fun onCombatantPressed(combatantViewModel: CombatantViewModel)
    fun onCombatantLongPressed(combatant: CombatantViewModel)
    fun onCombatantSwipedToEnd(combatantViewModel: CombatantViewModel): SwipeResponse
    fun onCombatantSwipedToStart(combatantViewModel: CombatantViewModel): SwipeResponse
    fun onDamageDialogSubmit(damage: Int)
    fun onAddNewPressed()
    fun nextCombatant()
}
