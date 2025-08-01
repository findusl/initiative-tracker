package de.lehrbaum.initiativetracker.bl

import de.lehrbaum.initiativetracker.bl.InternalsImmutableCollections.MAX_BUFFER_SIZE
import kotlinx.collections.immutable.ImmutableCollection
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList

/*
 * Need some typical functions on Immutable Collections.
 * There are some completely unnecessary optimizations in this file. But fun to think about.
 */

inline fun <T, R> ImmutableCollection<T>.map(crossinline transform: (T) -> R): PersistentList<R> {
	if (this.size <= MAX_BUFFER_SIZE) return (this as Iterable<T>).map(transform).toPersistentList()
	return asSequence().map { transform(it) }.toPersistentList() // slightly better performance for bigger lists
}

inline fun <T> ImmutableCollection<T>.filter(crossinline predicate: (T) -> Boolean): PersistentList<T> {
	if (this.size <= MAX_BUFFER_SIZE) return (this as Iterable<T>).filter(predicate).toPersistentList()
	return asSequence().filter { predicate(it) }.toPersistentList() // slightly better performance for bigger lists
}

object InternalsImmutableCollections {
	const val MAX_BUFFER_SIZE = 32 // internal in the library
}
