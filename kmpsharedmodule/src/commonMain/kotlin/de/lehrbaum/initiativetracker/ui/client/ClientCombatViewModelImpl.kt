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
import de.lehrbaum.initiativetracker.ui.character.CharacterChooserViewModel
import de.lehrbaum.initiativetracker.ui.edit.EditCombatantViewModel
import de.lehrbaum.initiativetracker.ui.edit.EditCombatantViewModelImpl
import de.lehrbaum.initiativetracker.ui.shared.CombatantViewModel
import de.lehrbaum.initiativetracker.ui.shared.SnackbarState
import de.lehrbaum.initiativetracker.ui.shared.SnackbarState.Text
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

data class ClientCombatViewModelImpl(
	override val sessionId: Int,
	private val leaveScreen: () -> Unit
): ClientCombatViewModel {
	private val combatSession = ClientCombatSession(sessionId)

	override val combatState = combatSession.state

	override val snackbarState = mutableStateOf<SnackbarState?>(null)

	override val ownerId = GeneralSettingsRepository.installationId

	override var characterChooserViewModel by mutableStateOf<CharacterChooserViewModel?>(null)
		private set

	override var editCombatantViewModel by mutableStateOf<EditCombatantViewModel?>(null)
		private set

	override var assignDamageCombatant by mutableStateOf<CombatantViewModel?>(null)
		private set

	override fun onCombatantClicked(combatantViewModel: CombatantViewModel) {
		snackbarState.value = Text("Combat press not implemented")
	}

	override fun onCombatantLongClicked(combatant: CombatantViewModel) {
		if (combatant.ownerId == ownerId) {
			editCombatant(combatant)
		} else {
			snackbarState.value = Text("Cannot edit characters not added by you.")
		}
	}

	private fun editCombatant(combatantViewModel: CombatantViewModel, firstEdit: Boolean = false) {
		editCombatantViewModel = EditCombatantViewModelImpl(
			combatantViewModel,
			firstEdit,
			onSave = {
				snackbarState.value = Text("Requesting to edit ${it.name}.", SnackbarDuration.Short)
				val result = combatSession.requestEditCharacter(it)
				if (result) {
					editCombatantViewModel = null
				} else {
					snackbarState.value = Text("Edit rejected", SnackbarDuration.Long)
				}
			},
			onCancel = { editCombatantViewModel = null }
		)
	}

	override suspend fun chooseCharacterToAdd() {
		val combatant = suspendCancellableCoroutine<CombatantModel> { continuation ->
			characterChooserViewModel = CharacterChooserViewModel(
				onChosen = { character, initiative, currentHp ->
					val combatant = character.run { CombatantModel(ownerId, id = -1, name, initiative, maxHp, currentHp) }
					snackbarState.value = Text("Requesting to add ${combatant.name}.", SnackbarDuration.Short)
					characterChooserViewModel = null
					continuation.resume(combatant)
				},
				onCancel = {
					characterChooserViewModel = null
				}
			)
			continuation.invokeOnCancellation { characterChooserViewModel = null }
		}
		val result = combatSession.requestAddCharacter(combatant)
		val message = if (result) "Added ${combatant.name} successfully." else "Adding ${combatant.name} rejected."
		snackbarState.value = Text(message, SnackbarDuration.Long)
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
