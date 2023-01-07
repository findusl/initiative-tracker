package de.lehrbaum.initiativetracker.extensions

import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

/**
 * Omits the unused view parameter of the listener.
 *
 * @see View.setOnClickListener
 */
fun View.setOnClickListener(listener: () -> Unit) {
	setOnClickListener { _: View -> listener() }
}

fun Fragment.showSnackbar(text: String, @BaseTransientBottomBar.Duration length: Int = Snackbar.LENGTH_SHORT) {
	view?.let {
		Snackbar.make(it, text, length).show()
	}
}
