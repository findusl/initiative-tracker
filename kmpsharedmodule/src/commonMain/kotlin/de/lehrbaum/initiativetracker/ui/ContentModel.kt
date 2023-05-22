package de.lehrbaum.initiativetracker.ui

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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
			//.flowOn(Dispatchers.IO) //no difference

	fun nextId() {
		navigateToDifferentId(Random.nextInt(10000))
	}
}