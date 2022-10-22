package de.lehrbaum.initiativetracker.view.combat

data class CombatantViewModel(
	val id: Long,
	val name: String,
	val initiative: Short,
	val editMode: Boolean = false,
	var active: Boolean = false
) : Comparable<CombatantViewModel> {

	val initiativeString: String
		get() = initiative.toString()

	override fun compareTo(other: CombatantViewModel): Int {
		var order = initiative - other.initiative
		if (order == 0)
			order = (id - other.id).toInt()
		return order
	}
}