package de.lehrbaum.initiativetracker.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import de.lehrbaum.initiativetracker.ui.model.main.MainModelImpl
import de.lehrbaum.initiativetracker.ui.screen.main.MainScreen
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

class MainActivity : ComponentActivity() {

	private val mainModel = MainModelImpl()

	init {
		// Initialize Napier
		Napier.base(DebugAntilog())
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContent {
			MainScreen(mainModel)
		}
	}
}
