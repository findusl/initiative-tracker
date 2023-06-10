package de.lehrbaum.initiativetracker.ui.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.remember
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

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
	override val coroutineContext = coroutineContext + Job()
	override fun onAbandoned() {
		cancel("Left composition")
	}
	override fun onForgotten() {
		cancel("Left composition")
	}
	override fun onRemembered() { }

}
