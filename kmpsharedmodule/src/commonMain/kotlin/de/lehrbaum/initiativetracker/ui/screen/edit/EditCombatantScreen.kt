package de.lehrbaum.initiativetracker.ui.screen.edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import de.lehrbaum.initiativetracker.ui.model.edit.EditCombatantModel
import de.lehrbaum.initiativetracker.ui.screen.Constants
import de.lehrbaum.initiativetracker.ui.screen.FullscreenDialog

@Composable
fun HostEditCombatantDialog(hostEditCombatantViewModel: EditCombatantModel) {
    FullscreenDialog(onDismissRequest = hostEditCombatantViewModel::onCancelPressed) {
        Scaffold(topBar = { DialogTopBar(hostEditCombatantViewModel) }) {
            HostEditCombatantScreen(hostEditCombatantViewModel, Modifier.padding(it))
        }
    }
}

@Composable
private fun DialogTopBar(hostEditCombatantViewModel: EditCombatantModel) {
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
fun HostEditCombatantScreen(hostEditCombatantViewModel: EditCombatantModel, modifier: Modifier = Modifier) {
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
