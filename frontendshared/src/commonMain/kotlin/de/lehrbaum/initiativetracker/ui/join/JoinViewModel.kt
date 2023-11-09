package de.lehrbaum.initiativetracker.ui.join

import androidx.compose.runtime.*
import de.lehrbaum.initiativetracker.GlobalInstances
import de.lehrbaum.initiativetracker.bl.InputValidator
import de.lehrbaum.initiativetracker.bl.data.Backend
import de.lehrbaum.initiativetracker.bl.data.CombatLink
import io.ktor.http.Url

@Stable
data class JoinViewModel(
	private val onJoin: (CombatLink) -> Unit,
	val asHost: Boolean
) {
	private val defaultBackend = GlobalInstances.generalSettingsRepository.defaultBackend
	val title = if(asHost) "Host existing combat" else "Join existing combat"

	var hostFieldContent by mutableStateOf("${defaultBackend.hostUrl}:${defaultBackend.port}")
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
		val backend = Backend(secureConnectionChosen, url.host, url.port)
		val combatLink = CombatLink(backend, asHost, combatIdFieldParsed)
		onJoin(combatLink)
	}
}
