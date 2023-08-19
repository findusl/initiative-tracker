package de.lehrbaum.initiativetracker.ui.composables

import de.lehrbaum.initiativetracker.ui.shared.CombatantViewModel

fun mockCombatant(
	ownerId: Long = 0,
	id: Long = 0,
	name: String = "Small Mock Creature",
	creatureType: String? = "Mocker",
	initiative: Int? = 10,
	maxHp: Int? = 20,
	currentHp: Int? = 10,
	disabled: Boolean = false,
	isHidden: Boolean = false,
	active: Boolean = false,
) =
	CombatantViewModel(ownerId, id, name, creatureType, initiative, maxHp, currentHp, disabled, isHidden, active)
