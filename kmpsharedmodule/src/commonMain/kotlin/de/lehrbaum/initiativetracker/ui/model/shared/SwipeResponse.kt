package de.lehrbaum.initiativetracker.ui.model.shared

enum class SwipeResponse(val dismissResponse: Boolean) {
	SLIDE_OUT(true),
	SLIDE_BACK(false)
}