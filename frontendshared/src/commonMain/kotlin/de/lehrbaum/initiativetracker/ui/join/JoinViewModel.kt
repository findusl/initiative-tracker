package de.lehrbaum.initiativetracker.ui.join

import androidx.compose.runtime.*
import de.lehrbaum.initiativetracker.bl.InputValidator
import de.lehrbaum.initiativetracker.bl.data.CombatLink
import io.ktor.http.Url

@Stable
data class JoinViewModel(
	private val onJoin: (CombatLink) -> Unit,
	val asHost: Boolean
) {
	val title = if(asHost) "Host existing combat" else "Join existing combat"

	var hostFieldContent by mutableStateOf("localhost")
	val hostFieldError by derivedStateOf { !InputValidator.isValidHost(hostFieldContent) }

	var combatIdFieldContent by mutableStateOf("")
	val combatIdFieldParsed by derivedStateOf { combatIdFieldContent.toIntOrNull() }
	val combatIdFieldError by derivedStateOf { combatIdFieldParsed == null && combatIdFieldContent.isNotEmpty() }

	var secureConnectionChosen by mutableStateOf(false)

	val inputsAreValid by derivedStateOf {
		(!hostFieldError && !combatIdFieldError)
	}

	fun onConnectPressed() {
		if (!inputsAreValid) return

		val protocol = if (secureConnectionChosen) "wss" else "ws"
		val url = Url("$protocol://$hostFieldContent")
		val combatLink = CombatLink(secureConnectionChosen, url.host, url.port, asHost, combatIdFieldParsed)
		onJoin(combatLink)
	}
}
