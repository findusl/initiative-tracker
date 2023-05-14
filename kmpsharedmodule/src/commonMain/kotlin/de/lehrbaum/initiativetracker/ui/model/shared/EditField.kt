package de.lehrbaum.initiativetracker.ui.model.shared

import androidx.compose.runtime.*
import androidx.compose.ui.text.input.KeyboardType

@Stable
data class EditField<T>(
	val initialValue: T,
	val keyboardType: KeyboardType? = null,
	val singleLine: Boolean = true,
	val parseInput: (String) -> Result<T>
) {
	var currentState by mutableStateOf(initialValue?.toString() ?: "")
	val hasError by derivedStateOf { parseInput(currentState).isFailure }
	val value: Result<T>
		get() = parseInput(currentState)

	companion object {
		fun <R> failure() = Result.Companion.failure<R>(dummy)
	}
}

private val dummy = RuntimeException("I don't know what to put")
