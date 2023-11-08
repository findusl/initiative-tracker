package de.lehrbaum.initiativetracker.bl.data

import com.russhwolf.settings.ExperimentalSettingsApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

private const val SETTINGS_KEY = "links"
private const val SETTINGS_NAME = "combatlink"

@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
object CombatLinkRepository {
	private val settings = createSettingsFactory().create(SETTINGS_NAME)
	val combatLinks = MutableStateFlow(loadCombatLinks())

	fun addCombatLink(combatLink: CombatLink) {
		combatLinks.value += combatLink
		persistCombatLinks()
	}

	fun removeCombatLink(combatLink: CombatLink) {
		combatLinks.value -= combatLink
		persistCombatLinks()
	}

	private fun persistCombatLinks() =
		settings.encodeValue(SETTINGS_KEY, combatLinks.value)

	private fun loadCombatLinks() =
		settings.decodeValue(SETTINGS_KEY, emptySet<CombatLink>())
}

@Serializable
data class CombatLink(
    val sessionId: Int,
    val isHost: Boolean
)
