package de.lehrbaum.initiativetracker.bl

import de.lehrbaum.initiativetracker.dtos.CombatantModel
import kotlinx.serialization.Serializable

sealed interface CombatCommand {
	@Serializable
	data class DamageCommand(val target: CombatantModel, val damage: Int): CombatCommand
}