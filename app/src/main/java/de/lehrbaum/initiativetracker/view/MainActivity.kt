package de.lehrbaum.initiativetracker.view

import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import de.lehrbaum.initiativetracker.R
import de.lehrbaum.initiativetracker.databinding.ActivityMainBinding
import de.lehrbaum.initiativetracker.view.combat.host.CombatHostFragmentDirections
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume


class MainActivity : AppCompatActivity() {

	private lateinit var appBarConfiguration: AppBarConfiguration
	private lateinit var binding: ActivityMainBinding

	init {
		// Initialize Napier
		Napier.base(DebugAntilog())
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)

		setSupportActionBar(binding.toolbar)

		val navController = findNavController(R.id.nav_host_fragment_content_main)
		appBarConfiguration = AppBarConfiguration(navController.graph)
		setupActionBarWithNavController(navController, appBarConfiguration)
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		// Inflate the menu; this adds items to the action bar if it is present.
		menuInflater.inflate(R.menu.menu_main, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		return when (item.itemId) {
			R.id.action_settings -> true
			R.id.action_join_combat -> {
				joinCombat()
				true
			}
			else -> super.onOptionsItemSelected(item)
		}
	}

	private fun joinCombat() {
		lifecycleScope.launch {
			val sessionId = requestSessionIdInput()
			val action = CombatHostFragmentDirections.actionCombatHostFragmentToCombatClientFragment(sessionId)
			findNavController(R.id.nav_host_fragment_content_main).navigate(action)
		}
	}

	private suspend fun requestSessionIdInput(): Int {
		val builder = AlertDialog.Builder(this)
		builder.setTitle("Please provide the SessionId")
		val input = EditText(this)
		input.inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL
		builder.setView(input)

		return suspendCancellableCoroutine { continuation ->
			builder.setPositiveButton("OK") { dialog, _ ->
				dialog.dismiss() // TODO handle invalid values
				val sessionId = input.text.toString().toInt()
				continuation.resume(sessionId)
			}
			builder.setNegativeButton("Cancel") { dialog, _ ->
				dialog.cancel()
				continuation.cancel()
			}

			builder.show()
		}
	}

	override fun onSupportNavigateUp(): Boolean {
		val navController = findNavController(R.id.nav_host_fragment_content_main)
		return navController.navigateUp(appBarConfiguration)
			|| super.onSupportNavigateUp()
	}
}
