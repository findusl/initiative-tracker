package de.lehrbaum.initiativetracker.ui.shared

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier

private const val TAG = "ErrorStateHolder"

interface ErrorStateHolder {
	val errorState: ErrorState?

	class Impl: ErrorStateHolder {
		override var errorState: ErrorState? by mutableStateOf(null)
			private set

		override fun <R> Result<R>.getOrNullAndHandle(customMessage: String?, logLevel: LogLevel?): R? =
			getOrElse { throwable ->
				logLevel?.let { Napier.log(it, TAG, throwable, customMessage ?: "no message") }
				errorState = ErrorState(customMessage, throwable)
				null
			}

		override fun Result<Unit>.handle(customMessage: String?, logLevel: LogLevel?) =
			onFailure { throwable ->
				logLevel?.let { Napier.log(it, TAG, throwable, customMessage ?: "no message") }
				errorState = ErrorState(customMessage, throwable)
			}

		override fun clearErrorState() {
			errorState = null
		}
	}

	fun <R> Result<R>.getOrNullAndHandle(customMessage: String? = null, logLevel: LogLevel? = LogLevel.WARNING): R?

	fun Result<Unit>.handle(customMessage: String? = null, logLevel: LogLevel? = LogLevel.WARNING): Result<Unit>

	fun clearErrorState()
}

data class ErrorState(val customMessage: String? = null, val failure: Throwable? = null)
