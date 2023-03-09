package de.lehrbaum.initiativetracker.kmpsharedmodule

class Greeting {
	private val platform: Platform = getPlatform()

	fun greet(): String {
		return "Hello, ${platform.name}!"
	}
}