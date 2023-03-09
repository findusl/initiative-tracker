package de.lehrbaum.initiativetracker.kmpsharedmodule

interface Platform {
	val name: String
}

expect fun getPlatform(): Platform