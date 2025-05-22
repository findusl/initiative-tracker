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

@Stable
class SettingsViewModel {
	private val generalSettingsRepository
		get() = GlobalInstances.generalSettingsRepository


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
		(!apiKeyFieldError) && (!newHomebrewLinkError)
	}

	fun onSavePressed() {
		if (!inputsAreValid) return

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
