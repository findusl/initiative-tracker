package de.lehrbaum.initiativetracker.ui.settings

import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import de.lehrbaum.initiativetracker.GlobalInstances
import de.lehrbaum.initiativetracker.bl.InputValidator
import de.lehrbaum.initiativetracker.data.BackendUri
import io.ktor.http.Url

@Stable
class SettingsViewModel {
	private val generalSettingsRepository
		get() = GlobalInstances.generalSettingsRepository

	private val defaultBackend = GlobalInstances.generalSettingsRepository.defaultBackendUri

	var hostFieldContent by mutableStateOf("${defaultBackend.hostName}:${defaultBackend.port}")
	val hostFieldError by derivedStateOf { !InputValidator.isValidHost(hostFieldContent) }

	var secureConnectionChosen by mutableStateOf(defaultBackend.secureConnection)

	var apiKeyFieldContent by mutableStateOf(generalSettingsRepository.openAiApiKey ?: "")
	val apiKeyFieldError by derivedStateOf {
		apiKeyFieldContent.isNotBlank() && !InputValidator.isValidOpenAiApiKey(apiKeyFieldContent)
	}

	var isBestiarySubmenuOpen by mutableStateOf(false)

	fun openSourcesSubmenu() {
		isBestiarySubmenuOpen = true
	}

	fun closeSourcesSubmenu() {
		isBestiarySubmenuOpen = false
	}

	var homebrewLinks: SnapshotStateList<String> = generalSettingsRepository.homebrewLinks.toMutableStateList()
	var newHomebrewLinkContent by mutableStateOf("")
	val newHomebrewLinkError by derivedStateOf {
		newHomebrewLinkContent.isNotBlank() && !InputValidator.isValidUrl(newHomebrewLinkContent)
	}

	val inputsAreValid by derivedStateOf {
		(!hostFieldError) && (!apiKeyFieldError) && (!newHomebrewLinkError)
	}

	fun onSavePressed() {
		if (!inputsAreValid) return

		val protocol = if (secureConnectionChosen) "https" else "http"
		val url = Url("$protocol://$hostFieldContent")
		val backendUri = BackendUri(secureConnectionChosen, url.host, url.port)
		generalSettingsRepository.defaultBackendUri = backendUri

		val apiKey = apiKeyFieldContent.trim()
		generalSettingsRepository.openAiApiKey = apiKey.ifBlank { null }

		generalSettingsRepository.homebrewLinks = homebrewLinks.toList()
	}

	fun addHomebrewLink() {
		if (newHomebrewLinkContent.isBlank() || newHomebrewLinkError) return

		homebrewLinks.add(newHomebrewLinkContent.trim())
		newHomebrewLinkContent = ""
	}

	fun removeHomebrewLink(link: String) {
		homebrewLinks.remove(link)
	}

	fun hideAllGuides() {
		generalSettingsRepository.hideAllGuides()
	}

	fun showAllGuides() {
		generalSettingsRepository.showAllGuides()
	}
}
