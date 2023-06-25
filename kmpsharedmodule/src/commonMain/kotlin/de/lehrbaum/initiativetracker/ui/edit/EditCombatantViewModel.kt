package de.lehrbaum.initiativetracker.ui.edit

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import de.lehrbaum.initiativetracker.bl.Dice
import de.lehrbaum.initiativetracker.bl.toModifier
import de.lehrbaum.initiativetracker.dtos.CombatantModel
import de.lehrbaum.initiativetracker.networking.bestiary.MonsterDTO
import de.lehrbaum.initiativetracker.ui.composables.ObservableMutableState
import de.lehrbaum.initiativetracker.ui.main.MainViewModel
import de.lehrbaum.initiativetracker.ui.shared.CombatantViewModel
import de.lehrbaum.initiativetracker.ui.shared.EditFieldViewModel

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

	var monsters by mutableStateOf(MainViewModel.monsters.value)

	var monsterTypeName: String by ObservableMutableState(combatantViewModel.creatureType ?: "", ::onMonsterTypeNameChanged)
	private var monsterType: MonsterDTO? by mutableStateOf(determineMonster(monsterTypeName))
	val monsterTypeError: Boolean by derivedStateOf { monsterType == null && monsterTypeName.isNotEmpty() }
	val monsterTypeNameSuggestions: List<String> by derivedStateOf {
		monsters
			.asSequence()
			.filter { it.displayName.contains(monsterTypeName) }
			.take(30)
			.map { it.displayName }
			.toList()
			.sortedBy { it.length }
	}
	var confirmApplyMonsterDialog: String? by mutableStateOf(null)

	private fun onMonsterTypeNameChanged(@Suppress("UNUSED_PARAMETER") old: String, new: String) {
		val determinedType = determineMonster(new)
		monsterType = determinedType
		if (determinedType != null) {
			if (initiativeEdit.isFailureOrNull()
				&& maxHpEdit.isFailureOrNull()
				&& currentHpEdit.isFailureOrNull()
				&& nameEdit.isFailureOrNull()
			) {
				applyMonsterType()
			} else {
				// TODO implement in ui
				confirmApplyMonsterDialog = new
			}
		}
	}

	private fun determineMonster(name: String): MonsterDTO? = monsters.firstOrNull { it.displayName == name }

	fun applyMonsterType() {
		val monsterType = this.monsterType
		monsterType?.hp?.average?.let { avgHp ->
			maxHpEdit.currentState = avgHp.toString()
			currentHpEdit.currentState = avgHp.toString()
		}
		monsterType?.dex?.let { dex ->
			initiativeEdit.currentState = (Dice.d20() + dex.toModifier()).toString()
		}
		monsterType?.name?.let { nameEdit.currentState = it }
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
