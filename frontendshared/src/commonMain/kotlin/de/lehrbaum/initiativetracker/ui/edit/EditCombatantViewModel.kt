package de.lehrbaum.initiativetracker.ui.edit

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.aallam.openai.api.BetaOpenAI
import de.lehrbaum.initiativetracker.GlobalInstances
import de.lehrbaum.initiativetracker.bl.Dice
import de.lehrbaum.initiativetracker.bl.toModifier
import de.lehrbaum.initiativetracker.dtos.CombatantModel
import de.lehrbaum.initiativetracker.networking.bestiary.MonsterDTO
import de.lehrbaum.initiativetracker.ui.main.MainViewModel
import de.lehrbaum.initiativetracker.ui.shared.CombatantViewModel
import de.lehrbaum.initiativetracker.ui.shared.EditFieldViewModel
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine

data class EditCombatantViewModel(
	private val combatantViewModel: CombatantViewModel,
	private val firstEdit: Boolean,
	private val onSave: suspend (CombatantModel) -> Unit,
	private val onCancel: () -> Unit,
) {
	val id = combatantViewModel.id
	val nameEdit = EditFieldViewModel(
		combatantViewModel.name,
		selectOnFirstFocus = firstEdit,
		parseInput = EditFieldViewModel.RequiredStringParser
	)
	val initiativeEdit = EditFieldViewModel(
		combatantViewModel.initiative,
		parseInput = EditFieldViewModel.OptionalIntParser
	)
	val maxHpEdit = EditFieldViewModel(
		combatantViewModel.maxHp,
		parseInput = EditFieldViewModel.OptionalIntParser
	)
	val currentHpEdit = EditFieldViewModel(
		combatantViewModel.currentHp,
		parseInput = EditFieldViewModel.OptionalIntParser
	)
	var isHidden: Boolean by mutableStateOf(combatantViewModel.isHidden)

	val monsters: List<MonsterDTO>
		get() = MainViewModel.Cache.monsters

	var monsterTypeName: String by mutableStateOf(combatantViewModel.creatureType ?: "")
	val monsterType: MonsterDTO? by derivedStateOf { determineMonster(monsterTypeName) }
	val monsterTypeError: Boolean by derivedStateOf { monsterType == null && monsterTypeName.isNotEmpty() }
	val monsterTypeNameSuggestions: List<String> by derivedStateOf {
		monsters
			.asSequence()
			.filter { it.displayName.contains(monsterTypeName, ignoreCase = true) }
			.take(30)
			.map { it.displayName }
			.filter { it != monsterTypeName } // Don't suggest the existing choice
			.toList()
			.sortedBy{ it.length }
	}
	var confirmApplyMonsterDialog: CancellableContinuation<Boolean>? by mutableStateOf(null)

	private fun determineMonster(name: String): MonsterDTO? =
		MainViewModel.Cache.getMonsterByName(name)

	suspend fun onMonsterTypeChanged(type: MonsterDTO?) {
		if (type != null) {
			if (initiativeEdit.isFailureOrNull()
				&& maxHpEdit.isFailureOrNull()
				&& currentHpEdit.isFailureOrNull()
				&& nameEdit.isFailureOrNull()
			) {
				applyMonsterType()
			} else {
				val confirmed = suspendCancellableCoroutine<Boolean> {
					confirmApplyMonsterDialog = it
					it.invokeOnCancellation { confirmApplyMonsterDialog = null }
				}
				if (confirmed)
					applyMonsterType()
				confirmApplyMonsterDialog = null
			}
		}
	}

	@Suppress("OPT_IN_IS_NOT_ENABLED")
	@OptIn(BetaOpenAI::class)
	private suspend fun applyMonsterType() {
		val monsterType = this.monsterType ?: return
		monsterType.hp?.average?.let { avgHp ->
			maxHpEdit.currentState = avgHp.toString()
			currentHpEdit.currentState = avgHp.toString()
		}
		monsterType.dex?.let { dex ->
			initiativeEdit.currentState = (Dice.d20() + dex.toModifier()).toString()
		}
		nameEdit.loadSuggestion {
			GlobalInstances.openAiNetworkClient?.suggestMonsterName(monsterType.name)
		}
	}

	suspend fun saveCombatant() {
		onSave(CombatantModel(
			combatantViewModel.ownerId,
			id,
			nameEdit.value.getOrThrow(),
			initiativeEdit.value.getOrThrow(),
			maxHpEdit.value.getOrThrow(),
			currentHpEdit.value.getOrThrow(),
			monsterType?.displayName,
			combatantViewModel.disabled,
			isHidden,
		))
    }

    fun cancel() {
        onCancel()
    }
}

private fun EditFieldViewModel<*>.isFailureOrNull() = value.getOrNull() == null
