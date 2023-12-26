package de.lehrbaum.initiativetracker.ui.keyevents

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester

/**
 * Used to make a component focussed by default for key inputs.
 */
@Composable
fun Modifier.defaultFocussed(key1: Any?): Modifier {
	val focusRequester = remember(key1) { FocusRequester() }
	LaunchedEffect(key1) {
		focusRequester.requestFocus()
	}
	return this.focusRequester(focusRequester)
}
