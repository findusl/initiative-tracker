package de.lehrbaum.initiativetracker.ui.character

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import de.lehrbaum.initiativetracker.ui.Constants
import de.lehrbaum.initiativetracker.ui.GeneralDialog
import de.lehrbaum.initiativetracker.ui.composables.DiceRollingTextField
import de.lehrbaum.initiativetracker.ui.composables.EditTextField
import de.lehrbaum.initiativetracker.ui.composables.OkCancelButtonRow
import de.lehrbaum.initiativetracker.ui.shared.EditFieldViewModel
import de.lehrbaum.initiativetracker.ui.shared.EditFieldViewModel.Companion.RequiredIntParser

@Composable
fun CharacterChooserScreen(characterChooserViewModel: CharacterChooserViewModel) {
	val characters by characterChooserViewModel.characters.collectAsState(emptyList())
	var chosen by remember { mutableStateOf<CharacterViewModel?>(null) }

	Scaffold(topBar = { TopBar(characterChooserViewModel::cancel) }) {
		LazyColumn {
			val itemModifier = Modifier
				.padding(Constants.defaultPadding)
				.fillMaxWidth()
			items(characters, key = CharacterViewModel::id) { item ->
				CharacterListElement(item, itemModifier.clickable {
					chosen = item
				})
			}
		}
	}
	chosen?.let {
		ExtraInfoDialog(it, onComplete = characterChooserViewModel::onChosen, onCancel = {
			chosen = null
		})
	}
}

@Composable
private fun ExtraInfoDialog(chosen: CharacterViewModel, onComplete: (CharacterViewModel, Int, Int) -> Unit, onCancel: () -> Unit) {
	GeneralDialog(onDismissRequest = onCancel) {
		Surface(
			shape = RoundedCornerShape(16.dp),
			color = Color.White
		) {
			Column(modifier = Modifier.padding(Constants.defaultPadding)) {
				var initiativeValue by remember { mutableStateOf(0) }
				val initiativeIsValidNumber = remember { mutableStateOf(false) }
				DiceRollingTextField(
					label = "Initiative",
					initialText = "1#20+${chosen.initiativeMod ?: 0}",
					onNumberChanged = { initiativeValue = it },
					textIsValidNumber = initiativeIsValidNumber,
				)
				//EditTextField(initiativeField, "Initiative")
				val currentHpField = remember(chosen) {
					EditFieldViewModel(chosen.maxHp ?: 0, parseInput = RequiredIntParser)
				}
				EditTextField(currentHpField, "Current HP")

				val submittable = remember { derivedStateOf { initiativeIsValidNumber.value && !currentHpField.hasError } }

				OkCancelButtonRow(
					submittable,
					onCancel,
					onSubmit = { onComplete(chosen, initiativeValue, currentHpField.value.getOrThrow()) }
				)
			}
		}
	}
}

@Composable
private fun TopBar(onCancel: () -> Unit) {
	TopAppBar(
		title = { Text("Choose Character") },
		navigationIcon = {
			IconButton(onClick = onCancel) {
				Icon(
					imageVector = Icons.Filled.Close,
					contentDescription = "Cancel edit"
				)
			}
		},
	)
}
