package de.lehrbaum.initiativetracker.ui.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.flow.Flow

interface ResettableState<T> : State<T> {
	fun reset()
}

@Composable
fun <T : R, R> Flow<T>.collectAsStateResettable(initial: R, context: CoroutineContext = EmptyCoroutineContext): ResettableState<R> {
	var numberOfResets by remember { mutableStateOf(0) }
	val coroutineContext = remember(context, numberOfResets) { context + CoroutineName("Restarted $numberOfResets times") }
	val state = collectAsState(initial, coroutineContext)
	return object : ResettableState<R>, State<R> by state {
		override fun reset() {
			numberOfResets++
		}
	}
}
