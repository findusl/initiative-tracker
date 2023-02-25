package de.lehrbaum.initiativetracker.view.combat.characters

import de.lehrbaum.initiativetracker.logic.CharacterModel

data class CharacterViewModel(
	val id: Long,
	val name: String,
	val initiativeMod: Int,
	val hitPoints: Int,
) {
	fun toModel(): CharacterModel = CharacterModel(id, name, initiativeMod, hitPoints)
}

fun CharacterModel.toCharacterViewModel() = CharacterViewModel(id, name, initiativeMod, hitPoints)
