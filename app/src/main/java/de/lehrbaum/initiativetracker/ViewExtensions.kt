package de.lehrbaum.initiativetracker

import android.view.View

/**
 * Omits the unused view parameter of the listener.
 *
 * @see View.setOnClickListener
 */
fun View.setOnClickListener(listener: () -> Unit) {
	setOnClickListener { _: View -> listener() }
}
