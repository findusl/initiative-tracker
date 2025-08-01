package de.lehrbaum.initiativetracker.dtos

import kotlinx.serialization.Serializable

@Serializable
data class CombatantId(val id: Long) : Comparable<CombatantId> {
	companion object {
		val UNKNOWN = CombatantId(-1)
	}

	override fun compareTo(other: CombatantId): Int = this.id.compareTo(other.id)
}
