package de.lehrbaum.initiativetracker.ui.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.lehrbaum.initiativetracker.ui.Constants
import de.lehrbaum.initiativetracker.ui.GeneralDialog
import de.lehrbaum.initiativetracker.ui.composables.AutocompleteTextField
import de.lehrbaum.initiativetracker.ui.composables.EditTextField
import de.lehrbaum.initiativetracker.ui.composables.OkCancelButtonRow
import de.lehrbaum.initiativetracker.ui.composables.composableIf
import kotlinx.coroutines.launch
import kotlin.coroutines.resume

@Composable
fun EditCombatantScreen(editCombatantViewModel: EditCombatantViewModel) {
	Scaffold(topBar = { DialogTopBar(editCombatantViewModel) }) {
		EditCombatantContent(editCombatantViewModel, Modifier.padding(it))
	}
	editCombatantViewModel.confirmApplyMonsterDialog?.let { completionContinuation ->
		confirmApplyMonsterDialog(
			onDismiss = { completionContinuation.resume(false) },
			onAccept = { completionContinuation.resume(true) },
			editCombatantViewModel
		)
	}
}

@Composable
private fun DialogTopBar(editCombatantViewModel: EditCombatantViewModel) {
	val canSave by remember {
		derivedStateOf {
			!editCombatantViewModel.run {
				nameError
					|| initiativeEdit.hasError
					|| maxHpEdit.hasError
					|| currentHpEdit.hasError
					|| monsterTypeError
			}
		}
	}
	val coroutineScope = rememberCoroutineScope()
	var showLoadingSpinner by remember { mutableStateOf(false) }
	TopAppBar(
		title = { Text(editCombatantViewModel.id.id.toString()) },
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
		CreatureTypeField(editCombatantViewModel)
		NameField(editCombatantViewModel)
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

@Composable
fun CreatureTypeField(editCombatantViewModel: EditCombatantViewModel) {
	var firstRun by remember(editCombatantViewModel) { mutableStateOf(true) }
	LaunchedEffect(editCombatantViewModel.monsterType) {
		if (firstRun) // skip on first run because user just opened the edit dialog
			firstRun = false
		else
			editCombatantViewModel.onMonsterTypeChanged(editCombatantViewModel.monsterType)
	}
	AutocompleteTextField(
		text = editCombatantViewModel.monsterTypeName,
		label = "Monster Type",
		onTextChanged = { editCombatantViewModel.monsterTypeName = it },
		error = editCombatantViewModel.monsterTypeError,
		suggestions = editCombatantViewModel.monsterTypeNameSuggestions,
		placeholder = "Skeleton (MM)",
		enabled = editCombatantViewModel.monsters.isNotEmpty()
	)
}

@Composable
fun NameField(editCombatantViewModel: EditCombatantViewModel) {
	AutocompleteTextField(
		text = editCombatantViewModel.name,
		label = "Name",
		onTextChanged = { editCombatantViewModel.name = it },
		error = editCombatantViewModel.nameError,
		suggestions = editCombatantViewModel.nameSuggestionsToShow,
		trailingIcon = composableIf (editCombatantViewModel.nameLoading) {
			CircularProgressIndicator(color = MaterialTheme.colors.primary,)
		}
	)
}

@Composable
private fun confirmApplyMonsterDialog(
	onDismiss: () -> Unit,
	onAccept: () -> Unit,
	editCombatantViewModel: EditCombatantViewModel
) {
	GeneralDialog(onDismissRequest = onDismiss) {
		Column(
			modifier = Modifier.padding(16.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			Text(text = "Apply stats of ${editCombatantViewModel.monsterTypeName} where known?")
			OkCancelButtonRow(true, onDismiss, onAccept)
		}
	}
}
