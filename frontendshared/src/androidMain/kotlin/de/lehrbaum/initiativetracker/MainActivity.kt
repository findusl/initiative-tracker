package de.lehrbaum.initiativetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import de.lehrbaum.initiativetracker.ui.main.MainComposable
import de.lehrbaum.initiativetracker.ui.main.MainViewModel
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

class MainActivity : ComponentActivity() {

	private val viewModel by viewModels<AndroidMainViewModel>()

	init {
		// Initialize Napier
		Napier.base(DebugAntilog())
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContent {
			MainComposable(viewModel.mainModel)
		}
	}
}

class AndroidMainViewModel: ViewModel() {
	val mainModel = MainViewModel()
}
