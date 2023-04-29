package de.lehrbaum.initiativetracker.view.characters

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.lehrbaum.initiativetracker.ui.screen.Constants
import de.lehrbaum.initiativetracker.ui.screen.Constants.defaultPadding
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun EditCharacterScreen(editCharacterViewModel: EditCharacterViewModel) {
	val characterViewModel by editCharacterViewModel.characterViewModel.collectAsStateWithLifecycle()
	val isNameError by editCharacterViewModel.isNameError.collectAsStateWithLifecycle()
	val isHitPointError by editCharacterViewModel.isNameError.collectAsStateWithLifecycle()


	Column(modifier = Modifier
		.padding(defaultPadding)
		.fillMaxWidth()) {
		OutlinedTextField(
			value = characterViewModel.name,
			onValueChange = { editCharacterViewModel.onNameUpdated(it) },
			label = { Text("Name") },
			isError = isNameError,
			modifier = Modifier.fillMaxWidth()
		)
		Spacer(modifier = Modifier.height(defaultPadding))
		OutlinedTextField(
			value = characterViewModel.initiativeModDisplayString,
			onValueChange = { editCharacterViewModel.onInitiativeModUpdated(it) },
			label = { Text("Initiative Modifier") },
			keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
			modifier = Modifier.fillMaxWidth()
		)
		Spacer(modifier = Modifier.height(defaultPadding))
		OutlinedTextField(
			value = characterViewModel.hitPointsDisplayString,
			onValueChange = { editCharacterViewModel.onHitPointsUpdated(it) },
			label = { Text("Hitpoints") },
			keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
			isError = isHitPointError,
			modifier = Modifier.fillMaxWidth()
		)
		Spacer(modifier = Modifier.height(defaultPadding))
		Button(
			onClick = { editCharacterViewModel.saveCharacter() },
			modifier = Modifier.fillMaxWidth()
		) {
			Text("Save")
		}
	}
}

private val defaultCharacter = CharacterViewModel(id = 1L, name = "John Doe", initiativeMod = 3, hitPoints = 10)

@Preview(device = Devices.NEXUS_5, showBackground = true, showSystemUi = true)
@Composable
fun EditCharacterScreenPreview() {

	EditCharacterScreen(MockEditCharacterViewModel(defaultCharacter))
}

@SuppressLint("StateFlowValueCalledInComposition")
@Preview(device = Devices.NEXUS_5, showBackground = true, showSystemUi = true)
@Composable
fun EditCharacterScreenPreviewError() {
	val mockViewModel = MockEditCharacterViewModel(defaultCharacter.copy(initiativeMod = 0))
	mockViewModel.isNameError.value = true

	EditCharacterScreen(mockViewModel)
}

private class MockEditCharacterViewModel(characterViewModel: CharacterViewModel) :
	EditCharacterViewModel {
	override val characterViewModel = MutableStateFlow(characterViewModel)
	override val isNameError = MutableStateFlow(false)
	override val isHitPointsError = MutableStateFlow(false)

	override fun onNameUpdated(name: String) {}

	override fun onInitiativeModUpdated(initiativeMod: String) {}

	override fun onHitPointsUpdated(hitPoints: String) {}

	override fun saveCharacter() {}

}
