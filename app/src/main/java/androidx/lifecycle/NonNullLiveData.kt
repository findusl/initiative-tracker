@file:Suppress("unused")

package androidx.lifecycle

import androidx.annotation.CallSuper
import androidx.annotation.MainThread


/**
 * I developed this class at some point during work to avoid all the null checks with LiveData
 */
open class NonNullLiveData<T : Any>(
	initialValue: T
) : LiveData<T>() {

	init {
		super.setValue(initialValue)
	}

	/**
	 * @see LiveData#getValue
	 */
	override fun getValue(): T {
		return super.getValue()!!
	}

	/**
	 * @see LiveData#observe
	 *
	 * @return the created Observer that can be removed using [LiveData.removeObserver]
	 */
	fun observe(owner: LifecycleOwner, function: (T) -> Unit): Observer<T> {
		val observer: Observer<T> = Observer { t -> function(t) }
		observe(owner, observer)
		return observer
	}

	/**
	 * @see LiveData#observeForever
	 *
	 * @return the created Observer that can be removed using [LiveData.removeObserver]
	 */
	fun observeForever(function: (T) -> Unit): Observer<T> {
		val observer: Observer<T> = Observer { t -> function(t) }
		observeForever(observer)
		return observer
	}

	/**
	 * @see LiveData#observe
	 */
	@Suppress("RedundantOverride")//needed for kotlin to java Compatibility
	override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
		super.observe(owner, observer)
	}

	fun <O : Any> map(function: (T) -> O): NonNullLiveData<O> {
		return mapToMediator(function)
	}

	/**
	 * Sometimes it is necessary to set the value on the mapped live data,
	 * for example if you want to reEmit the value by calling value = value.
	 * So this function guarantees a modifiable result.
	 */
	fun <O : Any> mapToMediator(function: (T) -> O): MediatorNonNullLiveData<O> {
		val result = MediatorNonNullLiveData(function(this.value))
		result.addSource(this) { result.value = function(it) }
		return result
	}

	fun <O : Any> mapOnChanged(function: (T) -> O): NonNullLiveData<O> {
		val result = MediatorNonNullLiveData(function(this.value))
		result.addSource(this) { result.setValueIfChanged(function(it)) }
		return result
	}
}

open class MutableNonNullLiveData<T : Any>(
	initialValue: T
) : NonNullLiveData<T>(initialValue) {
	/**
	 * @see MutableLiveData#setValue
	 */
	public override fun setValue(value: T) {
		super.setValue(value)
	}

	/**
	 * @see MutableLiveData#postValue
	 */
	public override fun postValue(value: T) {
		super.postValue(value)
	}
}

fun <T, O : Any> LiveData<T>.mapToNonNull(function: (T?) -> O): NonNullLiveData<O> {
	val result = MediatorNonNullLiveData(function(this.value))
	result.addLiveDataSource(this) { result.value = function(it) }
	return result
}

fun <T, O : Any> LiveData<T>.mapToNonNullOnChange(function: (T?) -> O): NonNullLiveData<O> {
	val result = MediatorNonNullLiveData(function(this.value))
	result.addLiveDataSource(this) {
		result.setValueIfChanged(function(it))
	}
	return result
}

/**
 * Creates a LiveData, let's name it `swLiveData`, which follows this flow:
 * It reacts on changes of `trigger` LiveData, applies the given function to new value of
 * `trigger` LiveData and sets resulting LiveData as a "backing" LiveData
 * to `swLiveData`.
 * "Backing" LiveData means, that all events emitted by it will retransmitted
 * by `swLiveData`.
 *
 * @see [LiveData.switchMap]
 *
 * @param function    a function which creates "backing" LiveData
 * @param <I>     a type of this LiveData
 * @param <O>     a type of resulting LiveData
 */
fun <T : Any, O : Any> NonNullLiveData<T>.switchMap(function: (T) -> NonNullLiveData<O>): NonNullLiveData<O> {
	var currentSource: NonNullLiveData<O> = function(value)
	val result = MediatorNonNullLiveData(currentSource.value)
	result.addSource(currentSource, result::setValue)
	result.addSource(this) {
		val newLiveData = function(it)
		if (currentSource != newLiveData) {
			result.removeSource(currentSource)
			currentSource = newLiveData
			result.addSource(currentSource, result::setValue)
		}
	}
	return result
}

/**
 * Maps the LiveData to a LiveData containing any supertype of the generic parameter. For example can be used
 * to hide a MutableList behind a List.
 *
 * Since a LiveData object does not publicly expose any setter functions, you can map it to any supertype
 * of the generic parameter. This is completely type safe, as the value cannot be set on the resulting LiveData.
 */
fun <O : Any, T : O> NonNullLiveData<T>.mapSupertype(): NonNullLiveData<O> {
	val subTypeLiveData = this
	return object : NonNullLiveData<O>(value) {
		override fun observe(owner: LifecycleOwner, observer: Observer<in O>) {
			subTypeLiveData.observe(owner, observer)
		}

		override fun observeForever(observer: Observer<in O>) {
			subTypeLiveData.observeForever(observer)
		}

		override fun removeObserver(observer: Observer<in O>) {
			subTypeLiveData.removeObserver(observer)
		}

		override fun removeObservers(owner: LifecycleOwner) {
			subTypeLiveData.removeObservers(owner)
		}

		override fun getValue(): O {
			return subTypeLiveData.value
		}

		override fun hasObservers(): Boolean {
			return subTypeLiveData.hasObservers()
		}

		override fun hasActiveObservers(): Boolean {
			return subTypeLiveData.hasActiveObservers()
		}
	}
}

/**
 * Sets the given value if it is different from the current value.
 */
fun <T : Any> MutableNonNullLiveData<T>.setValueIfChanged(value: T) {
	if (this.value != value) {
		this.value = value
	}
}

open class MediatorNonNullLiveData<T : Any>(
	initialValue: T
) : MutableNonNullLiveData<T>(initialValue) {
	private val sources = mutableMapOf<LiveData<*>, ISourceHelper<*>>()

	/**
	 * @see MediatorLiveData#addSource
	 */
	@MainThread
	fun <S : Any> addSource(source: NonNullLiveData<S>, function: (S) -> Unit) {
		val sourceTuple = SourceHelper(source, function)
		addSource(source, function, sourceTuple)
	}

	/**
	 * @see MediatorLiveData#addSource
	 */
	@MainThread
	fun <S> addLiveDataSource(source: LiveData<S>, function: (S?) -> Unit) {
		val sourceTuple = LDSourceHelper(source, function)
		addSource(source, function, sourceTuple)
	}

	private fun <S> addSource(
		source: LiveData<S>,
		function: Any,
		sourceTuple: ISourceHelper<*>
	) {
		val existing = sources[source]
		if (existing != null && existing.observer !== function) {
			throw IllegalArgumentException(
				"This source was already added with a different observer"
			)
		}
		if (existing != null) {
			return
		}
		sources[source] = sourceTuple
		if (hasActiveObservers()) {
			sourceTuple.plug()
		}
	}

	/**
	 * @see MediatorLiveData
	 */
	@MainThread
	fun <S> removeSource(toRemove: LiveData<S>) {
		val source = sources.remove(toRemove)
		source?.unplug()
	}

	@CallSuper
	override fun onActive() {
		for ((_, value) in sources) {
			value.plug()
		}
	}

	@CallSuper
	override fun onInactive() {
		for ((_, value) in sources) {
			value.unplug()
		}
	}

	private interface ISourceHelper<V> : Observer<V> {
		val observer: Any
		fun plug()
		fun unplug()
	}

	private class SourceHelper<V : Any>(
		val liveData: NonNullLiveData<V>,
		override val observer: (V) -> Unit
	) : ISourceHelper<V> {
		private var version = START_VERSION

		override fun plug() {
			liveData.observeForever(this)
		}

		override fun unplug() {
			liveData.removeObserver(this)
		}

		override fun onChanged(value: V) {
			if (version != liveData.version) {
				version = liveData.version
				observer(value)
			}
		}
	}

	private class LDSourceHelper<V>(
		val liveData: LiveData<V>,
		override val observer: (V?) -> Unit
	) : ISourceHelper<V?> {
		private var version = START_VERSION

		override fun plug() {
			liveData.observeForever(this)
		}

		override fun unplug() {
			liveData.removeObserver(this)
		}

		override fun onChanged(value: V?) {
			if (version != liveData.version) {
				version = liveData.version
				observer(value)
			}
		}
	}
}