package de.lehrbaum.initiativetracker.bl

import kotlin.random.Random

object Dice {
	fun d20() = Random.nextInt(20) + 1
}