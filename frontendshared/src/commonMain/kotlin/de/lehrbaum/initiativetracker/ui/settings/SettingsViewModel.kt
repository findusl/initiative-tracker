package de.lehrbaum.initiativetracker.ui.settings

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import de.lehrbaum.initiativetracker.GlobalInstances
import de.lehrbaum.initiativetracker.bl.InputValidator
import de.lehrbaum.initiativetracker.data.BackendUri
import io.ktor.http.Url

class SettingsViewModel {
	private val generalSettingsRepository
		get() = GlobalInstances.generalSettingsRepository

	private val defaultBackend = GlobalInstances.generalSettingsRepository.defaultBackendUri

	var hostFieldContent by mutableStateOf("${defaultBackend.hostName}:${defaultBackend.port}")
	val hostFieldError by derivedStateOf { !InputValidator.isValidHost(hostFieldContent) }

	var secureConnectionChosen by mutableStateOf(defaultBackend.secureConnection)

	val inputsAreValid by derivedStateOf {
		(!hostFieldError)
	}

	fun onSavePressed() {
		if (!inputsAreValid) return

		val protocol = if (secureConnectionChosen) "https" else "http"
		val url = Url("$protocol://$hostFieldContent")
		// TASK Should update the text field
		val backendUri = BackendUri(secureConnectionChosen, url.host, url.port)
		generalSettingsRepository.defaultBackendUri = backendUri
	}

}
