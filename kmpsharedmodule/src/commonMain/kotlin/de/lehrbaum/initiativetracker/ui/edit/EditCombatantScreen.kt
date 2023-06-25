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
import kotlinx.coroutines.launch

@Composable
fun EditCombatantScreen(editCombatantViewModel: EditCombatantViewModel) {
	Scaffold(topBar = { DialogTopBar(editCombatantViewModel) }) {
		EditCombatantContent(editCombatantViewModel, Modifier.padding(it))
	}
}

@Composable
private fun DialogTopBar(editCombatantViewModel: EditCombatantViewModel) {
	val canSave by derivedStateOf {
		!editCombatantViewModel.run {
			nameEdit.hasError
				|| initiativeEdit.hasError
				|| maxHpEdit.hasError
				|| currentHpEdit.hasError
		}
	}
	val coroutineScope = rememberCoroutineScope()
	var showLoadingSpinner by remember { mutableStateOf(false) }
    TopAppBar(
        title = { Text(editCombatantViewModel.id.toString()) },
        navigationIcon = {
            IconButton(onClick = editCombatantViewModel::cancel) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Cancel edit"
                )
            }
        },
        actions = {
            Button(
				onClick = {
					if (showLoadingSpinner) return@Button
					coroutineScope.launch {
						showLoadingSpinner = true
						editCombatantViewModel.saveCombatant()
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
private fun EditCombatantContent(editCombatantViewModel: EditCombatantViewModel, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(Constants.defaultPadding)
            .fillMaxWidth()
    ) {
        EditTextField(editCombatantViewModel.nameEdit, "name")
        Spacer(modifier = Modifier.height(Constants.defaultPadding))
		Row(horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
			EditTextField(editCombatantViewModel.initiativeEdit, "Initiative", Modifier.weight(1f))
			Spacer(modifier = Modifier.width(Constants.defaultPadding))
			Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
				Checkbox(
					editCombatantViewModel.isHidden,
					onCheckedChange = { editCombatantViewModel.isHidden = it },
				)
				Text(text = "Hidden")
			}
		}
        Spacer(modifier = Modifier.height(Constants.defaultPadding))
		Row(horizontalArrangement = Arrangement.SpaceEvenly) {
			EditTextField(editCombatantViewModel.maxHpEdit, "Max Hitpoints", Modifier.weight(1f))
			Spacer(modifier = Modifier.width(Constants.defaultPadding))
			EditTextField(editCombatantViewModel.currentHpEdit, "Current Hitpoints", Modifier.weight(1f))
		}

    }
}
