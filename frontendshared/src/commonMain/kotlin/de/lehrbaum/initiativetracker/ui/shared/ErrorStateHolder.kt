package de.lehrbaum.initiativetracker.ui.shared

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier

private const val TAG = "ErrorStateHolder"

interface ErrorStateHolder {
	var errorState: ErrorState?

	/**
	 * An experimental style, curious how it works out. The idea is to include this interface in classes via delegation.
	 * e.g. class Foo: ErrorStateHolder by Impl()
	 */
	class Impl: ErrorStateHolder {
		override var errorState: ErrorState? by mutableStateOf(null)
	}

	fun <R> Result<R>.getOrNullAndHandle(customMessage: String? = null, logLevel: LogLevel? = LogLevel.WARNING): R? =
		getOrElse { throwable ->
			logLevel?.let { Napier.log(it, TAG, throwable, customMessage ?: "no message") }
			errorState = ErrorState(customMessage, throwable)
			null
		}

	fun Result<Unit>.handle(customMessage: String? = null, logLevel: LogLevel? = LogLevel.WARNING) =
		onFailure { throwable ->
			logLevel?.let { Napier.log(it, TAG, throwable, customMessage ?: "no message") }
			errorState = ErrorState(customMessage, throwable)
		}
}

data class ErrorState(val customMessage: String? = null, val failure: Throwable? = null)
