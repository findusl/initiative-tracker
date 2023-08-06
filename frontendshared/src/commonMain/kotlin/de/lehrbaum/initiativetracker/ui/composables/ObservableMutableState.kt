package de.lehrbaum.initiativetracker.ui.composables

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class ObservableMutableState<T>(
	private val state: MutableState<T>,
	private val onChange: (old: T, new: T) -> Unit
): ReadWriteProperty<Any?, T>, State<T> by state {

	constructor(initialValue: T, onChange: (old: T, new: T) -> Unit): this(mutableStateOf(initialValue), onChange)

	override fun getValue(thisRef: Any?, property: KProperty<*>): T = value

	override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
		val oldValue = state.value
		if (value == oldValue) return
		state.value = value
		onChange(oldValue, value)
	}
}


