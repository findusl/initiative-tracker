package de.lehrbaum.initiativetracker.ui.composables

import de.lehrbaum.initiativetracker.ui.shared.CombatantViewModel

fun mockCombatant(
	mockIndex: Int = 0,
	ownerId: Long = mockIndex.toLong(),
	id: Long = mockIndex.toLong(),
	name: String = "Small Mock Creature $mockIndex",
	creatureType: String? = "Mocker$mockIndex",
	initiative: Int? = 10 + mockIndex,
	maxHp: Int? = 20 + mockIndex*10,
	currentHp: Int? = 15 + mockIndex,
	disabled: Boolean = false,
	isHidden: Boolean = false,
	active: Boolean = false,
) =
	CombatantViewModel(ownerId, id, name, creatureType, initiative, maxHp, currentHp, disabled, isHidden, active)
