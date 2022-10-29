package de.lehrbaum.initiativetracker.networking

import de.lehrbaum.initiativetracker.dtos.CombatantDTO
import de.lehrbaum.initiativetracker.logic.CombatantModel

fun CombatantModel.toDTO(): CombatantDTO = CombatantDTO(id, name, initiative)
