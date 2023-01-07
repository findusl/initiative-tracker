package de.lehrbaum.initiativetracker.networking

import de.lehrbaum.initiativetracker.dtos.CombatDTO
import de.lehrbaum.initiativetracker.dtos.CombatantDTO
import de.lehrbaum.initiativetracker.logic.CombatantModel

fun toCombatDTO(combatants: List<CombatantModel>, activeCombatantIndex: Int) =
	CombatDTO(activeCombatantIndex, combatants.map(CombatantModel::toDTO))

fun CombatantModel.toDTO(): CombatantDTO = CombatantDTO(id, name, initiative)

fun CombatantDTO.toModel(): CombatantModel = CombatantModel(id, name, initiative)
