package de.lehrbaum.initiativetracker.ui.character

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import de.lehrbaum.initiativetracker.ui.Constants
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
			if (characters.isEmpty()) {
				item {
					Text("No Characters available")
				}
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
	Dialog(onDismissRequest = onCancel) {
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
					submittable.value,
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
