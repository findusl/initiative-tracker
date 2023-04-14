package de.lehrbaum.initiativetracker.view.combat.client

data class ClientCombatantViewModel(
	val id: Long,
	val name: String,
	val initiative: Int,
	val editMode: Boolean = false,
	var active: Boolean = false
) : Comparable<ClientCombatantViewModel> {

	val initiativeString: String
		get() = initiative.toString()

	override fun compareTo(other: ClientCombatantViewModel): Int {
		var order = initiative - other.initiative
		if (order == 0)
			order = (id - other.id).toInt()
		return order
	}
}