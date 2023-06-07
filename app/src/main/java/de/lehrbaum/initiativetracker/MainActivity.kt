package de.lehrbaum.initiativetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.platform.LocalConfiguration
import de.lehrbaum.initiativetracker.ui.main.MainModelImpl
import de.lehrbaum.initiativetracker.ui.main.MainScreen
import de.lehrbaum.initiativetracker.ui.shared.ProvideScreenSizeInformation
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
			ProvideScreenSizeInformation(
				LocalConfiguration.current.screenWidthDp,
				LocalConfiguration.current.screenHeightDp,
			) {
				MaterialTheme {
					MainScreen(mainModel)
				}
			}
		}
	}
}
