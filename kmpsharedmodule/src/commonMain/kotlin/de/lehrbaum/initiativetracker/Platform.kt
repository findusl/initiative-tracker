package de.lehrbaum.initiativetracker

interface Platform {
	val name: String
}

expect fun getPlatform(): Platform