package de.lehrbaum.initiativetracker.ui

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlin.random.Random

data class ContentModel(
	val id: Int,
	private val navigateToDifferentId: (Int) -> Unit
)  {

	val connectionState: Flow<Boolean>
		get() = flow {
			this.emit(false)
			delay(10)
			this.emit(true)
		}
			.distinctUntilChanged()
			.flowOn(Dispatchers.IO)

	suspend fun onShareClicked() {
		delay(10)
		navigateToDifferentId(Random.nextInt(10000))
	}
}