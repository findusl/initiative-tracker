package de.lehrbaum.initiativetracker.ui.shared

import androidx.compose.runtime.*
import androidx.compose.ui.text.input.KeyboardType

@Stable
// cannot be a data class. That breaks stuff if different edit dialogs are shown
// with a field with the same value. Then compose optimizes
class EditField<T>(
	initialValue: T,
	val keyboardType: KeyboardType? = null,
	val singleLine: Boolean = true,
	val placeholder: String? = null,
	val selectOnFirstFocus: Boolean = false,
	val parseInput: (String) -> Result<T>
) {
	val initialValueText = initialValue?.toString() ?: ""
	/** Implemented EditTextField lazily, currently this value does not update the UI, only takes updates */
	var currentState by mutableStateOf(initialValueText)
	val hasError by derivedStateOf { parseInput(currentState).isFailure }
	val value: Result<T>
		get() = parseInput(currentState)

	companion object {
		fun <R> failedParsing() = Result.Companion.failure<R>(dummy)

		val RequiredIntParser: (String) -> Result<Int> =
			{ input -> input.toIntOrNull()?.let { Result.success(it) } ?: failedParsing() }

		val OptionalIntParser: (String) -> Result<Int?> =
			{ input ->
				if (input.isEmpty()) Result.success(null)
				else { input.toIntOrNull()?.let { Result.success(it) } ?: failedParsing() }
			}
	}
}

private val dummy = RuntimeException("I don't know what to write here")
