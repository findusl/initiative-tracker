package de.lehrbaum.initiativetracker.ui.character

import de.lehrbaum.initiativetracker.bl.model.CharacterModel

data class CharacterViewModel(
	val id: Long,
	val name: String,
	val initiativeMod: Int?,
	val maxHp: Int?,
)

fun CharacterModel.toCharacterViewModel() = CharacterViewModel(id, name, initiativeMod, maxHp)
