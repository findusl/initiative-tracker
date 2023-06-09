package de.lehrbaum.initiativetracker.ui.client

import androidx.compose.material.SnackbarDuration
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import de.lehrbaum.initiativetracker.bl.ClientCombatSession
import de.lehrbaum.initiativetracker.bl.data.CombatLink
import de.lehrbaum.initiativetracker.bl.data.CombatLinkRepository
import de.lehrbaum.initiativetracker.bl.data.GeneralSettingsRepository
import de.lehrbaum.initiativetracker.bl.model.CombatantModel
import de.lehrbaum.initiativetracker.ui.character.CharacterChooserModel
import de.lehrbaum.initiativetracker.ui.edit.EditCombatantModel
import de.lehrbaum.initiativetracker.ui.edit.EditCombatantModelImpl
import de.lehrbaum.initiativetracker.ui.shared.CombatantViewModel
import de.lehrbaum.initiativetracker.ui.shared.SnackbarState
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

data class ClientCombatModelImpl(override val sessionId: Int, private val leaveScreen: () -> Unit): ClientCombatModel {
	private val combatSession = ClientCombatSession(sessionId)

	override val combatState = combatSession.state

	override val snackbarState = mutableStateOf<SnackbarState?>(null)

	private val ownerId = GeneralSettingsRepository.installationId

	override var characterChooserModel by mutableStateOf<CharacterChooserModel?>(null)
		private set

	override var editCombatantModel by mutableStateOf<EditCombatantModel?>(null)
		private set

	override var assignDamageCombatant by mutableStateOf<CombatantViewModel?>(null)
		private set

	override fun onCombatantClicked(combatantViewModel: CombatantViewModel) {
		snackbarState.value = SnackbarState.Text("Combat press not implemented")
	}

	override fun onCombatantLongClicked(combatant: CombatantViewModel) {
		if (combatant.ownerId == ownerId) {
			editCombatant(combatant)
		} else {
			snackbarState.value = SnackbarState.Text("Cannot edit characters not added by you.")
		}
	}

	private fun editCombatant(combatantViewModel: CombatantViewModel, firstEdit: Boolean = false) {
		editCombatantModel = EditCombatantModelImpl(
			combatantViewModel,
			firstEdit,
			onSave = {
				snackbarState.value = SnackbarState.Text("Requesting to change ${it.name} in background.", SnackbarDuration.Short)
				editCombatantModel = null
				TODO("Implement")
			},
			onCancel = { editCombatantModel = null }
		)
	}

	override suspend fun chooseCharacterToAdd() {
		val combatant = suspendCancellableCoroutine<CombatantModel> { continuation ->
			characterChooserModel = CharacterChooserModel(
				onChosen = { character, initiative, currentHp ->
					val combatant = character.run { CombatantModel(ownerId, id = -1, name, initiative, maxHp, currentHp) }
					snackbarState.value = SnackbarState.Text("Requesting to add ${combatant.name}.", SnackbarDuration.Short)
					characterChooserModel = null
					continuation.resume(combatant)
				},
				onCancel = {
					characterChooserModel = null
				}
			)
		}
		val result = combatSession.requestAddCharacter(combatant)
		val message = if (result) "Added ${combatant.name} successfully." else "Adding ${combatant.name} rejected."
		snackbarState.value = SnackbarState.Text(message, SnackbarDuration.Long)
	}

	override fun onDamageDialogSubmit(damage: Int) {
		TODO("Not yet implemented")
	}

	override fun onDamageDialogCancel() {
		TODO("Not yet implemented")
	}

	override fun leaveCombat() {
		CombatLinkRepository.removeCombatLink(CombatLink(sessionId, isHost = false))
		leaveScreen()
		// theoretically by leaving the screen it should remove the flow from the Composition
		// thereby cancelling the collection which in turn should cancel the Websocket connection
		// which removes the client from the combat.
		// If this client added characters to the combat we might have to send a remove command first
	}
}
