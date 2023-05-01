package de.lehrbaum.initiativetracker.extensions

import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

fun Fragment.showSnackbar(text: String, @BaseTransientBottomBar.Duration length: Int = Snackbar.LENGTH_SHORT) {
	view?.let {
		Snackbar.make(it, text, length).show()
	}
}
