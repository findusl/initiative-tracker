package de.lehrbaum.initiativetracker.bl.model

data class CombatantModel(
	val id: Long,
	val name: String,
	val initiative: Int,
	val maxHp: Int = 0,
	val currentHp: Int = 0,
	val disabled: Boolean = false,
)

fun Iterable<CombatantModel>.sortByInitiative() =
	sortedWith(compareByDescending(CombatantModel::initiative).thenBy(CombatantModel::id))
