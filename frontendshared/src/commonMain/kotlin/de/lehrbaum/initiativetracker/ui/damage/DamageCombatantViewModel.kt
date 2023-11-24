package de.lehrbaum.initiativetracker.ui.damage

import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlin.math.roundToInt

@Stable
data class DamageCombatantViewModel(
	val target: String,
	private val onSubmit: suspend (Int) -> Unit,
	val onCancel: () -> Unit,
	val initialDamage: Int = 1
) {
	var sliderValue by mutableStateOf(initialDamage.toFloat())
	val sliderValueInt by derivedStateOf { sliderValue.roundToInt() }
	val textIsValidNumber = mutableStateOf(false)
	var isSubmitting by mutableStateOf(false)
		private set

	suspend fun onSubmit() {
		if (!textIsValidNumber.value || isSubmitting) return
		isSubmitting = true
		onSubmit(sliderValueInt)
		isSubmitting = false
	}

}
