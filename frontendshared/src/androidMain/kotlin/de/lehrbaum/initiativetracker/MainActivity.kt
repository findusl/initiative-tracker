package de.lehrbaum.initiativetracker

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import de.lehrbaum.initiativetracker.ui.main.MainComposable
import de.lehrbaum.initiativetracker.ui.main.MainViewModel
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

class MainActivity : ComponentActivity() {
	private val viewModel by viewModels<AndroidMainViewModel>()
	private val localNetworkPermissionLauncher =
		registerForActivityResult(ActivityResultContracts.RequestPermission()) {
			// Network failures are already surfaced by the connection screens when access is denied.
		}

	init {
		// Initialize Napier
		Napier.base(DebugAntilog())
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		requestLocalNetworkPermission()
		setContent {
			MainComposable(viewModel.mainModel)
		}
	}

	private fun requestLocalNetworkPermission() {
		if (
			Build.VERSION.SDK_INT >= 37 &&
			ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_LOCAL_NETWORK) !=
			PackageManager.PERMISSION_GRANTED
		) {
			localNetworkPermissionLauncher.launch(Manifest.permission.ACCESS_LOCAL_NETWORK)
		}
	}
}

class AndroidMainViewModel : ViewModel() {
	val mainModel = MainViewModel()
}
