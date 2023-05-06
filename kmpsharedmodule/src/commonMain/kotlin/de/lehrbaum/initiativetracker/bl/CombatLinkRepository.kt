package de.lehrbaum.initiativetracker.bl

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.serialization.decodeValue
import com.russhwolf.settings.serialization.encodeValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer

private const val SETTINGS_KEY = "links"
private const val SETTINGS_NAME = "combatlink"

@Suppress("OPT_IN_IS_NOT_ENABLED")
@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
object CombatLinkRepository {
	private val settings = SettingsFactory().create(SETTINGS_NAME)
	private val serializer = ListSerializer(CombatLink.serializer())
	val combatLinks = MutableStateFlow(loadCombatLinks())

	fun addCombatLink(combatLink: CombatLink) {
		synchronized(combatLinks) {
			combatLinks.value += combatLink
			persistCombatLinks()
		}
	}

	fun removeCombatLink(combatLink: CombatLink) {
		synchronized(combatLinks) {
			combatLinks.value = combatLinks.value - combatLink
			persistCombatLinks()
		}
	}

	private fun persistCombatLinks() {
		settings.encodeValue(serializer, SETTINGS_KEY, combatLinks.value)
	}

	private fun loadCombatLinks() =
		settings.decodeValue(serializer, SETTINGS_KEY, emptyList())
}

@Serializable
data class CombatLink(
	val combatId: Int,
	val isHost: Boolean
)
