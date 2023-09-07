package de.lehrbaum.initiativetracker.ui.shared

import androidx.compose.runtime.Composable

@Composable
actual fun BackHandler(enabled: Boolean, onBack: () -> Unit) {
	// no hardware back button on iOS
}
