package de.lehrbaum.initiativetracker.bl

import de.lehrbaum.initiativetracker.dtos.CombatModel
import de.lehrbaum.initiativetracker.dtos.CombatantModel

fun toCombatDTO(combatants: List<CombatantModel>, activeCombatantIndex: Int) =
	CombatModel(activeCombatantIndex, combatants.map(CombatantModel::toDTO))

fun CombatantModel.toDTO(): CombatantModel =
	CombatantModel(ownerId, id, name, initiative, maxHp, currentHp, creatureType, disabled, isHidden)

fun CombatantModel.toModel(): CombatantModel =
	CombatantModel(ownerId, id, name, initiative, maxHp, currentHp, creatureType, disabled, isHidden)
