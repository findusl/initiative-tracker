package de.lehrbaum.initiativetracker.ui.edit

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.lehrbaum.initiativetracker.ui.Constants
import de.lehrbaum.initiativetracker.ui.composables.EditTextField
import de.lehrbaum.initiativetracker.ui.composables.rememberCoroutineScope
import kotlinx.coroutines.launch

@Composable
fun EditCombatantScreen(editCombatantModel: EditCombatantModel) {
	Scaffold(topBar = { DialogTopBar(editCombatantModel) }) {
		EditCombatantContent(editCombatantModel, Modifier.padding(it))
	}
}

@Composable
private fun DialogTopBar(editCombatantModel: EditCombatantModel) {
	val canSave by derivedStateOf {
		!editCombatantModel.run {
			nameEdit.hasError
				|| initiativeEdit.hasError
				|| maxHpEdit.hasError
				|| currentHpEdit.hasError
		}
	}
	val coroutineScope = rememberCoroutineScope()
	var showLoadingSpinner by remember { mutableStateOf(false) }
    TopAppBar(
        title = { Text(editCombatantModel.id.toString()) },
        navigationIcon = {
            IconButton(onClick = editCombatantModel::cancel) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Cancel edit"
                )
            }
        },
        actions = {
            Button(
				onClick = {
					coroutineScope.launch {
						showLoadingSpinner = true
						editCombatantModel.saveCombatant()
						showLoadingSpinner = false
					}
				},
				enabled = canSave
			) {
				Row(verticalAlignment = Alignment.CenterVertically) {
					Text("Save")
					// Values found by trial and error. Might not work everywhere
					if (showLoadingSpinner) {
						CircularProgressIndicator(
							color = MaterialTheme.colors.onPrimary,
							strokeWidth = 3.dp,
							modifier = Modifier.padding(start = 8.dp).size(28.dp)
						)
					}
				}
            }
        }
    )
}

@Composable
private fun EditCombatantContent(editCombatantModel: EditCombatantModel, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(Constants.defaultPadding)
            .fillMaxWidth()
    ) {
        EditTextField(editCombatantModel.nameEdit, "name")
        Spacer(modifier = Modifier.height(Constants.defaultPadding))
		Row(horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
			EditTextField(editCombatantModel.initiativeEdit, "Initiative", Modifier.weight(1f))
			Spacer(modifier = Modifier.width(Constants.defaultPadding))
			Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
				Checkbox(
					editCombatantModel.isHidden,
					onCheckedChange = { editCombatantModel.isHidden = it },
				)
				Text(text = "Hidden")
			}
		}
        Spacer(modifier = Modifier.height(Constants.defaultPadding))
		Row(horizontalArrangement = Arrangement.SpaceEvenly) {
			EditTextField(editCombatantModel.maxHpEdit, "Max Hitpoints", Modifier.weight(1f))
			Spacer(modifier = Modifier.width(Constants.defaultPadding))
			EditTextField(editCombatantModel.currentHpEdit, "Current Hitpoints", Modifier.weight(1f))
		}
    }
}
