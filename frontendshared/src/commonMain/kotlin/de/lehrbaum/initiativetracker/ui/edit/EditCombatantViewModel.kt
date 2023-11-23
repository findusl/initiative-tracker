package de.lehrbaum.initiativetracker.ui.edit

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.aallam.openai.api.BetaOpenAI
import de.lehrbaum.initiativetracker.GlobalInstances
import de.lehrbaum.initiativetracker.bl.Dice
import de.lehrbaum.initiativetracker.bl.MonsterCache
import de.lehrbaum.initiativetracker.bl.toModifier
import de.lehrbaum.initiativetracker.dtos.CombatantModel
import de.lehrbaum.initiativetracker.networking.bestiary.MonsterDTO
import de.lehrbaum.initiativetracker.networking.bestiary.accessWithFallback
import de.lehrbaum.initiativetracker.ui.shared.EditFieldViewModel
import kotlinx.coroutines.*

data class EditCombatantViewModel(
	private val combatantModel: CombatantModel,
	private val firstEdit: Boolean,
	private val onSave: suspend (CombatantModel) -> Unit,
	private val onCancel: () -> Unit,
) {
	val id = combatantModel.id
	var name: String by mutableStateOf(combatantModel.name)
	val nameError: Boolean by derivedStateOf { name.isBlank() }
	private var nameSuggestions: List<String> by mutableStateOf(emptyList())
	val nameSuggestionsToShow by derivedStateOf { nameSuggestions.filter { it != name } }
	var nameLoading: Boolean by mutableStateOf(false)
	private var nameLoadingJob: Job? = null

	val initiativeEdit = EditFieldViewModel(
		combatantModel.initiative,
		parseInput = EditFieldViewModel.OptionalIntParser
	)
	val maxHpEdit = EditFieldViewModel(
		combatantModel.maxHp,
		parseInput = EditFieldViewModel.OptionalIntParser
	)
	val currentHpEdit = EditFieldViewModel(
		combatantModel.currentHp,
		parseInput = EditFieldViewModel.OptionalIntParser
	)
	var isHidden: Boolean by mutableStateOf(combatantModel.isHidden)

	val monsters: List<MonsterDTO>
		get() = MonsterCache.monsters

	var monsterTypeName: String by mutableStateOf(combatantModel.creatureType ?: "")
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
		MonsterCache.getMonsterByName(name)

	suspend fun onMonsterTypeChanged(type: MonsterDTO?) {
		if (type != null) {
			if (initiativeEdit.isFailureOrNull()
				&& maxHpEdit.isFailureOrNull()
				&& currentHpEdit.isFailureOrNull()
			) {
				applyMonsterType()
			} else {
				val confirmed = suspendCancellableCoroutine {
					confirmApplyMonsterDialog = it
					it.invokeOnCancellation { confirmApplyMonsterDialog = null }
				}
				if (confirmed)
					applyMonsterType()
				confirmApplyMonsterDialog = null
			}
			loadNameSuggestions(type)
		}
	}

	private fun applyMonsterType() {
		val monsterType = this.monsterType ?: return
		monsterType.accessWithFallback({ hp?.average }, ::determineMonster)?.let { avgHp ->
			maxHpEdit.onTextUpdated(avgHp.toString())
			currentHpEdit.onTextUpdated(avgHp.toString())
		}
		monsterType.accessWithFallback({ dex }, ::determineMonster)?.let { dex ->
			initiativeEdit.onTextUpdated((Dice.d20() + dex.toModifier()).toString())
		}
	}

	@OptIn(BetaOpenAI::class)
	private suspend fun loadNameSuggestions(monsterType: MonsterDTO) {
		cancelLoading()

		coroutineScope {
			nameLoadingJob = this.coroutineContext.job
			try {
				nameLoading = true
				val suggestions = GlobalInstances.openAiNetworkClient?.suggestMonsterNames(monsterType.name)
				if (!isActive) return@coroutineScope
				if (suggestions != null) {
					nameSuggestions = suggestions
					suggestions.firstOrNull()?.let {
						if (name.isBlank()) name = it
					}
				}
			} finally {
				nameLoading = false
			}
		}
	}

	private fun cancelLoading() {
		nameLoadingJob?.cancel()
	}

	suspend fun saveCombatant() {
		onSave(CombatantModel(
			combatantModel.ownerId,
			id,
			name,
			initiativeEdit.value.getOrThrow(),
			maxHpEdit.value.getOrThrow(),
			currentHpEdit.value.getOrThrow(),
			monsterType?.displayName,
			combatantModel.disabled,
			isHidden,
		))
    }

    fun cancel() {
        onCancel()
    }
}

private fun EditFieldViewModel<*>.isFailureOrNull() = value.getOrNull() == null
