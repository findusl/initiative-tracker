package de.lehrbaum.initiativetracker.ui.shared

import androidx.compose.runtime.*
import androidx.compose.ui.text.input.KeyboardType
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.job

@Stable
// cannot be a data class. That breaks stuff if different edit dialogs are shown with the same values.
// Then compose optimizes and does not actually rerun the EditTextField composable.
class EditFieldViewModel<T>(
	initialValue: T,
	val keyboardType: KeyboardType? = null,
	val singleLine: Boolean = true,
	val placeholder: String? = null,
	val selectOnFirstFocus: Boolean = false,
	val parseInput: (String) -> Result<T>,
) {
	var currentState by mutableStateOf(initialValue?.toString() ?: "")
		private set
	val hasError by derivedStateOf { parseInput(currentState).isFailure }
	val value: Result<T>
		get() = parseInput(currentState)
	var loading by mutableStateOf(false)
		private set

	private var loadingJob: Job? = null

	suspend fun loadSuggestion(loader: suspend () -> String?) {
		cancelLoading()

		coroutineScope {
			loadingJob = this.coroutineContext.job
				try {
				loading = true
				val suggestion = loader()
				if (!isActive) return@coroutineScope
				if (suggestion != null) {
					currentState = suggestion
				}
			} finally {
				loading = false
			}
		}
	}

	fun onTextUpdated(newText: String) {
		cancelLoading()
		currentState = newText
	}

	private fun cancelLoading() {
		loadingJob?.cancel()
	}

	companion object {
		fun <R> failedParsing() = Result.Companion.failure<R>(dummy)

		val RequiredIntParser: (String) -> Result<Int> =
			{ input -> input.toIntOrNull()?.let { Result.success(it) } ?: failedParsing() }

		val RequiredStringParser: (String) -> Result<String> =
			{ input -> if (input.isBlank()) failedParsing() else Result.success(input) }

		val OptionalIntParser: (String) -> Result<Int?> =
			{ input ->
				if (input.isEmpty()) Result.success(null)
				else { input.toIntOrNull()?.let { Result.success(it) } ?: failedParsing() }
			}
	}
}

private val dummy = RuntimeException("I don't know what to write here")
