package de.lehrbaum.initiativetracker.ui.composables

import androidx.compose.material.DrawerState
import androidx.compose.material.DrawerValue
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.remember
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

/*
Some default remember functions do not take a key as parameter. However, since sometimes the same kind of screen can
be shown one after the other with different content, the remember functions need to be reset. They need a key.
 */

/**
 * Remember a [CoroutineScope] with the given [key]. The Scope uses a SupervisorJob.
 */
@Composable
inline fun rememberCoroutineScope(
	key: Any?,
	crossinline getContext: @DisallowComposableCalls () -> CoroutineContext =
		{ Dispatchers.Main }
) =
	remember(key) { CoroutineWrapper(getContext()) }

class CoroutineWrapper(
	coroutineContext: CoroutineContext
) : RememberObserver, CoroutineScope {
	override val coroutineContext = coroutineContext + SupervisorJob()
	override fun onAbandoned() {
		cancel("Left composition")
	}
	override fun onForgotten() {
		cancel("Left composition")
	}
	override fun onRemembered() { }
}

@Composable
fun rememberScaffoldState(
	key: Any? = null,
	drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed),
	snackbarHostState: SnackbarHostState = remember(key) { SnackbarHostState() }
): ScaffoldState = remember(key) {
	ScaffoldState(drawerState, snackbarHostState)
}
