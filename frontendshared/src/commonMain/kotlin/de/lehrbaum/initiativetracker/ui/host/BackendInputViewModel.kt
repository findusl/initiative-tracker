package de.lehrbaum.initiativetracker.ui.host

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import de.lehrbaum.initiativetracker.GlobalInstances
import de.lehrbaum.initiativetracker.bl.InputValidator
import de.lehrbaum.initiativetracker.bl.data.Backend
import io.ktor.http.Url

data class BackendInputViewModel(
	private val onBackendConfirmed: suspend (Backend) -> Unit,
	val onDismiss: () -> Unit
) {

	private val defaultBackend = GlobalInstances.generalSettingsRepository.defaultBackend
	val title = "On which backend do you want to share?"

	var hostFieldContent by mutableStateOf("${defaultBackend.hostUrl}:${defaultBackend.port}")
	val hostFieldError by derivedStateOf { !InputValidator.isValidHost(hostFieldContent) }

	var secureConnectionChosen by mutableStateOf(defaultBackend.secureConnection)

	var isSubmitting by mutableStateOf(false)
		private set

	val inputsAreValid by derivedStateOf {
		(!hostFieldError)
	}

	suspend fun onConnectPressed() {
		if (!inputsAreValid) return
		if (isSubmitting) return
		isSubmitting = true

		val protocol = if (secureConnectionChosen) "https" else "http"
		val url = Url("$protocol://$hostFieldContent")
		val backend = Backend(secureConnectionChosen, url.host, url.port)
		onBackendConfirmed(backend)
		isSubmitting = false
	}
}
