package de.lehrbaum.initiativetracker.view.combat.characters

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun EditCharacterScreen(editCharacterViewModel: EditCharacterViewModel) {
	val characterViewModel by editCharacterViewModel.characterViewModel.collectAsStateWithLifecycle()
	val isNameError by editCharacterViewModel.isNameError.collectAsStateWithLifecycle()
	val isHitPointError by editCharacterViewModel.isNameError.collectAsStateWithLifecycle()

	Column(
		modifier = Modifier
			.padding(16.dp)
			.fillMaxWidth(),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.SpaceBetween
	) {
		OutlinedTextField(
			value = characterViewModel.name,
			onValueChange = { editCharacterViewModel.onNameUpdated(it) },
			label = { Text("Name") },
			isError = isNameError,
			modifier = Modifier.fillMaxWidth()
		)
		Spacer(modifier = Modifier.height(16.dp))
		OutlinedTextField(
			value = characterViewModel.initiativeMod.toString(),
			onValueChange = { editCharacterViewModel.onInitiativeModUpdated(it.toIntOrNull() ?: 0) },
			label = { Text("Initiative Modifier") },
			keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
			modifier = Modifier.fillMaxWidth()
		)
		Spacer(modifier = Modifier.height(16.dp))
		OutlinedTextField(
			value = characterViewModel.hitPoints.toString(),
			onValueChange = { editCharacterViewModel.onHitPointsUpdated(it.toIntOrNull() ?: 0) },
			label = { Text("Hitpoints") },
			keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
			isError = isHitPointError,
			modifier = Modifier.fillMaxWidth()
		)
		Spacer(modifier = Modifier.height(16.dp))
		Button(
			onClick = { editCharacterViewModel.saveCharacter() },
			modifier = Modifier.fillMaxWidth()
		) {
			Text("Save")
		}
	}
}

private val defaultCharacter = CharacterViewModel(id = 1L, name = "John Doe", initiativeMod = 3, hitPoints = 10)

@Preview(device = Devices.NEXUS_5, showBackground = true)
@Composable
fun EditCharacterScreenPreview() {

	EditCharacterScreen(MockEditCharacterViewModel(defaultCharacter))
}

@SuppressLint("StateFlowValueCalledInComposition")
@Preview(device = Devices.NEXUS_5, showBackground = true)
@Composable
fun EditCharacterScreenPreviewError() {
	val mockViewModel = MockEditCharacterViewModel(defaultCharacter)
	mockViewModel.isNameError.value = true

	EditCharacterScreen(mockViewModel)
}

private class MockEditCharacterViewModel(characterViewModel: CharacterViewModel): EditCharacterViewModel {
	override val characterViewModel = MutableStateFlow(characterViewModel)
	override val isNameError = MutableStateFlow(false)

	override fun onNameUpdated(name: String) {

	}

	override fun onInitiativeModUpdated(initiativeMod: Int) {

	}

	override fun onHitPointsUpdated(hitPoints: Int) {

	}

	override fun saveCharacter() {

	}

}
