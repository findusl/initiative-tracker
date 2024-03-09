package de.lehrbaum.initiativetracker.ui.client

import androidx.compose.material.SnackbarDuration
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import de.lehrbaum.initiativetracker.GlobalInstances
import de.lehrbaum.initiativetracker.bl.ClientCombatSession
import de.lehrbaum.initiativetracker.data.CombatLink
import de.lehrbaum.initiativetracker.data.CombatLinkRepository
import de.lehrbaum.initiativetracker.bl.model.CharacterModel
import de.lehrbaum.initiativetracker.dtos.CombatantModel
import de.lehrbaum.initiativetracker.dtos.UserId
import de.lehrbaum.initiativetracker.ui.character.CharacterChooserViewModel
import de.lehrbaum.initiativetracker.ui.damage.DamageCombatantViewModel
import de.lehrbaum.initiativetracker.ui.edit.EditCombatantViewModel
import de.lehrbaum.initiativetracker.ui.shared.SnackbarState
import de.lehrbaum.initiativetracker.ui.shared.SnackbarState.Text
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

@Stable
data class ClientCombatViewModel(
    val combatLink: CombatLink,
    private val leaveScreen: () -> Unit
) {
	private val combatSession = ClientCombatSession(combatLink)

	val combatState = combatSession.state

	val snackbarState = mutableStateOf<SnackbarState?>(null)

	val title = "Joined ${combatLink.userDescription}"

	val ownerId = UserId(GlobalInstances.generalSettingsRepository.installationId)

	var characterChooserViewModel by mutableStateOf<CharacterChooserViewModel?>(null)
		private set

	var editCombatantViewModel by mutableStateOf<EditCombatantViewModel?>(null)
		private set

	private var assignDamageCombatant by mutableStateOf<CombatantModel?>(null)
	val damageCombatantViewModel by derivedStateOf { assignDamageCombatant?.let {
		DamageCombatantViewModel(it.name, ::onDamageDialogSubmit, ::onDamageDialogCancel)
	} }

	fun onCombatantClicked(combatantViewModel: CombatantModel) {
		assignDamageCombatant = combatantViewModel
	}

	fun onCombatantLongClicked(combatant: CombatantModel) {
		if (combatant.ownerId == ownerId) {
			editCombatant(combatant)
		} else {
			snackbarState.value = Text("Cannot edit characters not added by you.")
		}
	}

	private fun editCombatant(combatantViewModel: CombatantModel, firstEdit: Boolean = false) {
		editCombatantViewModel = EditCombatantViewModel(
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

	suspend fun chooseCharacterToAdd() {
		val combatant = suspendCancellableCoroutine<CombatantModel> { continuation ->
			characterChooserViewModel = CharacterChooserViewModel(
				onChosen = { character, initiative, currentHp ->
					val combatant = character.toCombatantModel(initiative, currentHp)
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

	private suspend fun onDamageDialogSubmit(damage: Int) {
		assignDamageCombatant?.let {
			val result = combatSession.requestDamageCharacter(it.id, damage, ownerId)
			if (result)
				assignDamageCombatant = null
		}
	}

	private fun onDamageDialogCancel() {
		assignDamageCombatant = null
	}

	fun leaveCombat() {
		CombatLinkRepository.removeCombatLink(combatLink)
		leaveScreen()
		// theoretically by leaving the screen it should remove the flow from the Composition
		// thereby cancelling the collection which in turn should cancel the Websocket connection
		// which removes the client from the combat.
		// If this client added characters to the combat we might have to send a remove command first
	}

	/*
	 * The active combatant should actually be added in the ViewModel, but since we don't have access to the flow here,
	 * we have to do it in the Screen. Maybe a StateFlow that is initialized by the view might be an idea.
	 */
	suspend fun finishTurn(activeCombatantIndex: Int) {
		combatSession.requestFinishTurn(activeCombatantIndex)
	}

	private fun CharacterModel.toCombatantModel(initiative: Int, currentHp: Int): CombatantModel {
		return CombatantModel(
			ownerId = ownerId,
			name = name,
			initiative = initiative,
			maxHp = maxHp,
			currentHp = currentHp,
		)
	}
}
