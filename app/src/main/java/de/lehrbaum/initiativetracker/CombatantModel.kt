package de.lehrbaum.initiativetracker

data class CombatantModel(
	val id: Long,
	val name: String,
	val initiative: Short,
)

fun Sequence<CombatantModel>.sortByInitiative() = sortedWith(compareBy(CombatantModel::initiative, CombatantModel::id))
