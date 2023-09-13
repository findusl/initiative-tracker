package de.lehrbaum.initiativetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import de.lehrbaum.initiativetracker.ui.main.MainScreen
import de.lehrbaum.initiativetracker.ui.main.MainViewModelImpl
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

class MainActivity : ComponentActivity() {

	private val mainModel = MainViewModelImpl()

	init {
		// Initialize Napier
		Napier.base(DebugAntilog())
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContent {
			MaterialTheme {
				MainScreen(mainModel)
			}
		}
	}
}
