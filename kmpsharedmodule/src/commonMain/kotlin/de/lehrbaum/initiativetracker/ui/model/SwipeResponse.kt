package de.lehrbaum.initiativetracker.ui.model

enum class SwipeResponse(val dismissResponse: Boolean) {
	SLIDE_OUT(true),
	SLIDE_BACK(false)
}