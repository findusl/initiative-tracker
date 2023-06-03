package de.lehrbaum.initiativetracker.bl.model

data class CombatantModel(
	val id: Long,
	val name: String,
	val initiative: Int? = null,
	val maxHp: Int? = null,
	val currentHp: Int? = null,
	val disabled: Boolean = false,
	val hidden: Boolean = false,
)

/**
 * Sorts predictably. First by initiative then by id. null initiatives are lower than any other initiatives
 */
fun Iterable<CombatantModel>.sortByInitiative() =
	sortedWith(compareByDescending(CombatantModel::initiative).thenBy(CombatantModel::id))
