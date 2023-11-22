package de.lehrbaum.initiativetracker.ui.character

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import de.lehrbaum.initiativetracker.ui.Constants
import de.lehrbaum.initiativetracker.ui.composables.EditTextField

@Composable
fun EditCharacterScreen(editCharacterModel: EditCharacterModel) {
	Scaffold(topBar = { DialogTopBar(editCharacterModel) }) {
		EditCharacterContent(editCharacterModel, Modifier.padding(it))
	}
}

@Composable
private fun DialogTopBar(editCharacterModel: EditCharacterModel) {
	val canSave by remember {
		derivedStateOf {
			!editCharacterModel.run { nameEdit.hasError || initiativeModEdit.hasError || maxHpEdit.hasError }
		}
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
private fun EditCharacterContent(editCharacterModel: EditCharacterModel, modifier: Modifier = Modifier) {
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
