package de.lehrbaum.initiativetracker.dtos

import kotlinx.serialization.Serializable

@Serializable
data class CombatantModel(
	val ownerId: UserId,
	val id: CombatantId = CombatantId.UNKNOWN,
	val name: String,
	val initiative: Int? = null,
	val maxHp: Int? = null,
	val currentHp: Int? = null,
	val creatureType: String? = null,
	val disabled: Boolean = false,
	val isHidden: Boolean = false,
	val strCheck: Int? = null,
	val strSave: Int? = null,
	val dexCheck: Int? = null,
	val dexSave: Int? = null,
	val conCheck: Int? = null,
	val conSave: Int? = null,
	val intCheck: Int? = null,
	val intSave: Int? = null,
	val wisCheck: Int? = null,
	val wisSave: Int? = null,
	val chaCheck: Int? = null,
	val chaSave: Int? = null,
)
