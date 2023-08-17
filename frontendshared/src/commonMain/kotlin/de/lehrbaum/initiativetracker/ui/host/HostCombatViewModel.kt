package de.lehrbaum.initiativetracker.ui.host

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import de.lehrbaum.initiativetracker.bl.HostConnectionState
import de.lehrbaum.initiativetracker.ui.composables.ConfirmDamageOptions
import de.lehrbaum.initiativetracker.ui.composables.DamageOption
import de.lehrbaum.initiativetracker.ui.edit.EditCombatantViewModel
import de.lehrbaum.initiativetracker.ui.shared.CombatantViewModel
import de.lehrbaum.initiativetracker.ui.shared.SnackbarState
import kotlinx.coroutines.flow.Flow

@Stable
interface HostCombatViewModel {
	val hostConnectionState: Flow<HostConnectionState>
    val combatants: Flow<List<CombatantViewModel>>
	val editCombatantViewModel: State<EditCombatantViewModel?>
    val assignDamageCombatant: MutableState<CombatantViewModel?>
    val snackbarState: MutableState<SnackbarState?>
	val confirmDamage: ConfirmDamageOptions?
	val combatStarted: Boolean
    val isSharing: Boolean
	val sessionId: Int

    fun onCombatantClicked(combatantViewModel: CombatantViewModel)
    fun onCombatantLongClicked(combatant: CombatantViewModel)
	fun deleteCombatant(combatantViewModel: CombatantViewModel)
	fun disableCombatant(combatantViewModel: CombatantViewModel)
	fun enableCombatant(combatantViewModel: CombatantViewModel)
    fun onDamageDialogSubmit(damage: Int)
    fun onDamageDialogCancel()
	fun onConfirmDamageDialogSubmit(option: DamageOption)
	fun onConfirmDamageDialogCancel()
    fun addNewCombatant()
    fun nextCombatant()
    fun previousCombatant()
	fun jumpToCombatant(combatantViewModel: CombatantViewModel)
    fun undoDelete()
    fun startCombat()
    suspend fun shareCombat()
	suspend fun closeSession()
	fun showSessionId()
}
