package de.lehrbaum.initiativetracker.ui.client

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import de.lehrbaum.initiativetracker.bl.ClientCombatState
import de.lehrbaum.initiativetracker.ui.character.CharacterChooserViewModel
import de.lehrbaum.initiativetracker.ui.edit.EditCombatantViewModel
import de.lehrbaum.initiativetracker.ui.shared.CombatantViewModel
import de.lehrbaum.initiativetracker.ui.shared.SnackbarState
import kotlinx.coroutines.flow.Flow

@Stable
interface ClientCombatViewModel {
	val combatState: Flow<ClientCombatState>
	val snackbarState: MutableState<SnackbarState?>
	val sessionId: Int
	val ownerId: Long // TODO this should be hidden in the ClientCombatViewModel in future
	val characterChooserViewModel: CharacterChooserViewModel?
	val editCombatantViewModel: EditCombatantViewModel?
	val assignDamageCombatant: CombatantViewModel?

	fun onCombatantClicked(combatantViewModel: CombatantViewModel)
	fun onCombatantLongClicked(combatant: CombatantViewModel)
	suspend fun chooseCharacterToAdd()
	fun onDamageDialogSubmit(damage: Int)
	fun onDamageDialogCancel()
	fun leaveCombat()
}