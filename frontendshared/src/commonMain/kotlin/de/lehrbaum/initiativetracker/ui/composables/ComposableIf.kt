package de.lehrbaum.initiativetracker.ui.composables

import androidx.compose.runtime.Composable

/**
 * Returns null if the condition is wrong, otherwise the content.
 * This replaces code like this, which is common in parameters for composable:
 *
 * `if (condition) {{ content }} else null`
 */
fun composableIf(condition: Boolean, content: @Composable () -> Unit): @Composable (() -> Unit)? =
	if (condition) { content } else null
