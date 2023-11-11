package de.lehrbaum.initiativetracker.bl.data

import com.russhwolf.settings.ExperimentalSettingsApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

private const val SETTINGS_KEY_V1 = "links"
private const val SETTINGS_KEY_V2 = "links_v2"
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
		settings.encodeValue(SETTINGS_KEY_V2, combatLinks.value)

	private fun loadCombatLinks(): Set<CombatLink> {
		settings.remove(SETTINGS_KEY_V1) // the old CombatLink did not specify the host, not convertible
		return settings.decodeValue(SETTINGS_KEY_V2, emptySet())
	}
}

@Serializable
data class CombatLink(
	val secureConnection: Boolean,
	val host: String,
	val port: Int,
	val isHost: Boolean,
	val sessionId: Int? = null,
)
