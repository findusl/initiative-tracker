package de.lehrbaum.initiativetracker.ui.model.character

import de.lehrbaum.initiativetracker.bl.model.CharacterModel

data class CharacterViewModel(
	val id: Long,
	val name: String,
	val initiativeMod: Int?,
	val maxHp: Int?,
) {
	val initiativeModDisplayString: String
		get() = initiativeMod?.toString() ?: ""
	val hitPointsDisplayString: String
		get() = maxHp?.toString() ?: ""

	fun toModel(): CharacterModel = CharacterModel(id, name, initiativeMod, maxHp)
}

fun CharacterModel.toCharacterViewModel() = CharacterViewModel(id, name, initiativeMod, maxHp)
