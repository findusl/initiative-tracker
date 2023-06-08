package de.lehrbaum.initiativetracker.bl

import de.lehrbaum.initiativetracker.bl.model.CombatantModel
import de.lehrbaum.initiativetracker.dtos.CombatDTO
import de.lehrbaum.initiativetracker.dtos.CombatantDTO

fun toCombatDTO(combatants: List<CombatantModel>, activeCombatantIndex: Int) =
	CombatDTO(activeCombatantIndex, combatants.map(CombatantModel::toDTO))

fun CombatantModel.toDTO(): CombatantDTO =
	CombatantDTO(ownerId, id, name, initiative, maxHp, currentHp, disabled, isHidden)

fun CombatantDTO.toModel(): CombatantModel =
	CombatantModel(ownerId, id, name, initiative, maxHp, currentHp, disabled, isHidden)
