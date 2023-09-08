package de.lehrbaum.initiativetracker.ui.composables

import androidx.compose.runtime.*
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

interface ResettableState<T>: State<T> {
	fun reset()
}

@Composable
fun <T : R, R> Flow<T>.collectAsStateResettable(
	initial: R,
	context: CoroutineContext = EmptyCoroutineContext
): ResettableState<R> {
	var numberOfResets by remember { mutableStateOf(0) }
	val coroutineContext = remember(context, numberOfResets) { context + CoroutineName("Restarted $numberOfResets times") }
	val state = collectAsState(initial, coroutineContext)
	return object : ResettableState<R>, State<R> by state {
		override fun reset() {
			numberOfResets++
		}
	}
}
