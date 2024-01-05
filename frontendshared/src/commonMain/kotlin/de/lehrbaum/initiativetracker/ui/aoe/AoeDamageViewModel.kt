package de.lehrbaum.initiativetracker.ui.aoe

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import de.lehrbaum.initiativetracker.bl.AoeOptions
import de.lehrbaum.initiativetracker.bl.map
import de.lehrbaum.initiativetracker.dtos.CombatantId
import de.lehrbaum.initiativetracker.dtos.CombatantModel
import kotlinx.collections.immutable.ImmutableCollection
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

@Stable
data class AoeDamageViewModel(
	private val possibleTargets: ImmutableList<CombatantModel>,
	private val onSubmit: suspend (AoeOptions, ImmutableCollection<CombatantId>) -> Boolean,
	private val onDismiss: () -> Unit
) {
	var targets by mutableStateOf(possibleTargets.map { TargetViewModel(it.name, false, it.id) })
		private set

	var isSubmitting by mutableStateOf(false)
		private set

	var activeTab by mutableStateOf(0)
	var damage by mutableStateOf(0)
	var isDamageValid by mutableStateOf(false)

	fun onTargetPressed(target: TargetViewModel) {
		targets = targets.map { element ->
			if (element.id == target.id) element.copy(isSelected = !element.isSelected) else element
		}
	}

	suspend fun onSubmitPressed() {
		isSubmitting = true
		val selectedTargets = targets.asSequence().filter { it.isSelected }.map { it.id }.toPersistentList()
		onSubmit(TODO(), selectedTargets)
		isSubmitting = false
	}

	fun onDismiss() {
		onDismiss()
	}
}

@Immutable
data class TargetViewModel(
	val name: String,
	val isSelected: Boolean,
	val id: CombatantId,
)
