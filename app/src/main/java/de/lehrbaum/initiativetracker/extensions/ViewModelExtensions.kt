package de.lehrbaum.initiativetracker.extensions

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

inline fun <reified ViewModelType : ViewModel> Fragment.viewModelsFactory(
	crossinline viewModelInitialization: () -> ViewModelType
): Lazy<ViewModelType> {
	return viewModels {
		object : ViewModelProvider.Factory {
			override fun <T : ViewModel> create(modelClass: Class<T>): T {
				@Suppress("UNCHECKED_CAST") // Sadly the Factory is not generic :/
				return viewModelInitialization() as T
			}
		}
	}
}
