package de.lehrbaum.initiativetracker.ui.character

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import de.lehrbaum.initiativetracker.ui.Constants
import de.lehrbaum.initiativetracker.ui.FullscreenDialog
import de.lehrbaum.initiativetracker.ui.composables.EditTextField

@Composable
fun EditCharacterDialog(editCharacterModel: EditCharacterModel) {
	FullscreenDialog(onDismissRequest = editCharacterModel::cancel) {
		Scaffold(topBar = { DialogTopBar(editCharacterModel) }) {
			EditCharacterScreen(editCharacterModel, Modifier.padding(it))
		}
	}
}

@Composable
private fun DialogTopBar(editCharacterModel: EditCharacterModel) {
	val canSave by derivedStateOf {
		!editCharacterModel.run { nameEdit.hasError || initiativeModEdit.hasError || maxHpEdit.hasError }
	}
	TopAppBar(
		title = {},
		navigationIcon = {
			IconButton(onClick = editCharacterModel::cancel) {
				Icon(
					imageVector = Icons.Filled.Close,
					contentDescription = "Cancel edit"
				)
			}
		},
		actions = {
			Button(onClick = editCharacterModel::saveCharacter, enabled = canSave) {
				Text("Save")
			}
		}
	)
}

@Composable
fun EditCharacterScreen(editCharacterModel: EditCharacterModel, modifier: Modifier = Modifier) {
	Column(
		modifier = modifier
			.padding(Constants.defaultPadding)
			.fillMaxWidth()
	) {
		EditTextField(editCharacterModel.nameEdit, "name")
		Spacer(modifier = Modifier.height(Constants.defaultPadding))
		Row(horizontalArrangement = Arrangement.SpaceEvenly) {
			EditTextField(editCharacterModel.initiativeModEdit, "Initiative Modifier", Modifier.weight(1f))
			Spacer(modifier = Modifier.width(Constants.defaultPadding))
			EditTextField(editCharacterModel.maxHpEdit, "Max Hitpoints", Modifier.weight(1f))
		}
	}
}
