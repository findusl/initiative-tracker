package de.lehrbaum.initiativetracker.bl

data class CombatantModel(
	val id: Long,
	val name: String,
	val initiative: Short,
)

fun Iterable<CombatantModel>.sortByInitiative() =
	sortedWith(compareByDescending(CombatantModel::initiative).thenBy(CombatantModel::id))
