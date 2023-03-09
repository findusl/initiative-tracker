package de.lehrbaum.initiativetracker.view

enum class SwipeResponse(val dismissResponse: Boolean) {
	SLIDE_OUT(true),
	SLIDE_BACK(false)
}