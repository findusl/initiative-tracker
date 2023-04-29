package de.lehrbaum.initiativetracker.view.combat.host

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import de.lehrbaum.initiativetracker.ui.screen.Constants

@Stable
interface HostEditCombatantViewModel {
	val name: MutableState<String>
	val nameError: MutableState<Boolean>
	val initiativeString: MutableState<String>
	val initiativeError: MutableState<Boolean>
	val maxHpString: MutableState<String>
	val maxHpError: MutableState<Boolean>
	val currentHpString: MutableState<String>
	val currentHpError: MutableState<Boolean>

	fun onSavePressed()
	fun onCancelPressed()
}

@Composable
fun HostEditCombatantDialog(hostEditCombatantViewModel: HostEditCombatantViewModel) {
	Dialog(
		properties = DialogProperties(usePlatformDefaultWidth = false),
		onDismissRequest = {}
	) {
		Scaffold(topBar = { DialogTopBar(hostEditCombatantViewModel) }) {
			HostEditCombatantScreen(hostEditCombatantViewModel, Modifier.padding(it))
		}
	}
}

@Composable
private fun DialogTopBar(hostEditCombatantViewModel: HostEditCombatantViewModel) {
	TopAppBar(
		title = {},
		navigationIcon = {
			IconButton(onClick = hostEditCombatantViewModel::onCancelPressed) {
				Icon(
					imageVector = Icons.Filled.Close,
					contentDescription = "Cancel edit"
				)
			}
		},
		actions = {
			Button(onClick = hostEditCombatantViewModel::onSavePressed) {
				Text("Save")
			}
		}
	)
}

@Composable
fun HostEditCombatantScreen(hostEditCombatantViewModel: HostEditCombatantViewModel, modifier: Modifier = Modifier) {
	Column(
		modifier = modifier
			.padding(Constants.defaultPadding)
			.fillMaxWidth()
	) {
		var name by hostEditCombatantViewModel.name
		var initative by hostEditCombatantViewModel.initiativeString
		var maxHp by hostEditCombatantViewModel.maxHpString
		var currentHp by hostEditCombatantViewModel.currentHpString
		OutlinedTextField(
			value = name,
			onValueChange = { name = it },
			label = { Text("Name") },
			isError = hostEditCombatantViewModel.nameError.value,
			modifier = Modifier.fillMaxWidth()
		)
		Spacer(modifier = Modifier.height(Constants.defaultPadding))
		OutlinedTextField(
			value = initative,
			onValueChange = { initative = it },
			label = { Text("Initiative Modifier") },
			isError = hostEditCombatantViewModel.initiativeError.value,
			keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
			singleLine = true,
			modifier = Modifier.fillMaxWidth()
		)
		Spacer(modifier = Modifier.height(Constants.defaultPadding))
		OutlinedTextField(
			value = maxHp,
			onValueChange = { maxHp = it },
			label = { Text("Maximum Hitpoints") },
			isError = hostEditCombatantViewModel.maxHpError.value,
			keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
			singleLine = true,
			modifier = Modifier.fillMaxWidth()
		)
		Spacer(modifier = Modifier.height(Constants.defaultPadding))
		OutlinedTextField(
			value = currentHp,
			onValueChange = { currentHp = it },
			label = { Text("Current Hitpoints") },
			isError = hostEditCombatantViewModel.currentHpError.value,
			keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
			singleLine = true,
			modifier = Modifier.fillMaxWidth()
		)
	}
}

internal class MockEditHostCombatantViewModel : HostEditCombatantViewModel {
	override val name = mutableStateOf("Combatant 1")
	override val nameError = mutableStateOf(false)
	override val initiativeString = mutableStateOf("2")
	override val initiativeError = mutableStateOf(false)
	override val maxHpString = mutableStateOf("10")
	override val maxHpError = mutableStateOf(false)
	override val currentHpString = mutableStateOf("10")
	override val currentHpError = mutableStateOf(false)

	override fun onSavePressed() {}
	override fun onCancelPressed() {}
}
