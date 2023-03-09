package de.lehrbaum.initiativetracker.view.combat.host

import de.lehrbaum.initiativetracker.bl.CombatantModel

data class HostCombatantViewModel(
	val id: Long,
	val name: String,
	val initiative: Short,
	val editMode: Boolean = false,
	var active: Boolean = false
) : Comparable<HostCombatantViewModel> {

	val initiativeString: String
		get() = initiative.toString()

	override fun compareTo(other: HostCombatantViewModel): Int {
		var order = initiative - other.initiative
		if (order == 0)
			order = (id - other.id).toInt()
		return order
	}

	fun toCombatantModel(): CombatantModel {
		return CombatantModel(id, name, initiative)
	}
}

fun CombatantModel.toHostCombatantViewModel(): HostCombatantViewModel {
	return HostCombatantViewModel(id, name, initiative)
}
