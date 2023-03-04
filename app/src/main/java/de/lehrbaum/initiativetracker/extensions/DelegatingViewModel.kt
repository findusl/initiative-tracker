package de.lehrbaum.initiativetracker.extensions

import androidx.annotation.MainThread
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.State.DESTROYED
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel

/**
 * A viewmodel that has a delegate which is removed when the associated Lifecycle is destroyed
 */
open class DelegatingViewModel<DelegateType> : ViewModel() {
	protected val delegate: DelegateType?
		get() = delegateWrapper?.first
	private var delegateWrapper: Pair<DelegateType, LifecycleOwner>? = null

	private val lifecycleObserver: LifecycleEventObserver = object : LifecycleEventObserver {
		override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
			if (source.lifecycle.currentState == DESTROYED) {
				delegateWrapper = null
				return
			}
		}
	}

	@MainThread
	fun setDelegate(delegate: DelegateType, owner: LifecycleOwner) {
		if (owner.lifecycle.currentState == DESTROYED) {
			return //ignore
		}
		delegateWrapper?.second?.lifecycle?.removeObserver(lifecycleObserver)
		this.delegateWrapper = Pair(delegate, owner)
		owner.lifecycle.addObserver(lifecycleObserver)
	}
}
