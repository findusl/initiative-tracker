package de.lehrbaum.initiativetracker.ui.edit

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
fun EditCombatantDialog(editCombatantModel: EditCombatantModel) {
    FullscreenDialog(onDismissRequest = editCombatantModel::cancel) {
        Scaffold(topBar = { DialogTopBar(editCombatantModel) }) {
            EditCombatantScreen(editCombatantModel, Modifier.padding(it))
        }
    }
}

@Composable
private fun DialogTopBar(editCombatantModel: EditCombatantModel) {
	val canSave by derivedStateOf {
		!editCombatantModel.run { nameEdit.hasError || initiativeEdit.hasError || maxHpEdit.hasError || currentHpEdit.hasError }
	}
    TopAppBar(
        title = {},
        navigationIcon = {
            IconButton(onClick = editCombatantModel::cancel) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Cancel edit"
                )
            }
        },
        actions = {
            Button(onClick = editCombatantModel::saveCombatant, enabled = canSave) {
                Text("Save")
            }
        }
    )
}

@Composable
fun EditCombatantScreen(editCombatantModel: EditCombatantModel, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(Constants.defaultPadding)
            .fillMaxWidth()
    ) {
        EditTextField(editCombatantModel.nameEdit, "name")
        Spacer(modifier = Modifier.height(Constants.defaultPadding))
        EditTextField(editCombatantModel.initiativeEdit, "Initiative")
        Spacer(modifier = Modifier.height(Constants.defaultPadding))
        EditTextField(editCombatantModel.maxHpEdit, "Max Hitpoints")
        Spacer(modifier = Modifier.height(Constants.defaultPadding))
        EditTextField(editCombatantModel.currentHpEdit, "Current Hitpoints")
    }
}
