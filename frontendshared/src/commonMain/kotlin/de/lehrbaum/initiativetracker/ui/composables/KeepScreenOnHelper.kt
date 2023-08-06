package de.lehrbaum.initiativetracker.ui.composables

import androidx.compose.runtime.Composable

/**
 * Keeps the Screen on, if the platform supports this.
 */
@Composable
expect fun KeepScreenOn()
