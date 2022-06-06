package de.lehrbaum.initiativetracker

import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("is_selected")
fun setSelected(view: View, selected: Boolean) {
	view.isSelected = selected
}
