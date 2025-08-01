package de.lehrbaum.initiativetracker.ui.join

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import de.lehrbaum.initiativetracker.GlobalInstances
import de.lehrbaum.initiativetracker.bl.InputValidator
import de.lehrbaum.initiativetracker.data.BackendUri
import de.lehrbaum.initiativetracker.data.CombatLink
import de.lehrbaum.initiativetracker.data.SessionId
import io.ktor.http.Url

data class JoinViewModel(
	private val onJoin: (CombatLink) -> Unit,
	val asHost: Boolean,
) {
	private val defaultBackend = GlobalInstances.generalSettingsRepository.defaultBackendUri
	val title = if (asHost) "Host existing combat" else "Join existing combat"

	var hostFieldContent by mutableStateOf("${defaultBackend.hostName}:${defaultBackend.port}")
	val hostFieldError by derivedStateOf { !InputValidator.isValidHost(hostFieldContent) }

	var combatIdFieldContent by mutableStateOf("")
	private val combatIdFieldParsed by derivedStateOf { combatIdFieldContent.toIntOrNull() }
	val combatIdFieldError by derivedStateOf { combatIdFieldParsed == null && combatIdFieldContent.isNotEmpty() }

	var secureConnectionChosen by mutableStateOf(defaultBackend.secureConnection)

	val inputsAreValid by derivedStateOf {
		(!hostFieldError && !combatIdFieldError)
	}

	fun onConnectPressed() {
		if (!inputsAreValid) return

		val protocol = if (secureConnectionChosen) "https" else "http"
		val url = Url("$protocol://$hostFieldContent")
		val backendUri = BackendUri(secureConnectionChosen, url.host, url.port)
		val combatLink = CombatLink(backendUri, asHost, combatIdFieldParsed?.let { SessionId(it) })
		onJoin(combatLink)
	}
}
