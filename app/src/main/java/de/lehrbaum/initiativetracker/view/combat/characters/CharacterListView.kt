package de.lehrbaum.initiativetracker.view.combat.characters

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

private val defaultPadding = 8.dp

@Composable
fun CharacterListView(
	characterListViewModel: CharacterListViewModel,
	modifier: Modifier = Modifier,
) {
	val charactersState by characterListViewModel.characters.collectAsStateWithLifecycle()

	LazyColumn(modifier) {
		val itemModifier = Modifier
			.padding(16.dp)
			.fillMaxWidth()
		items(charactersState, key = CharacterViewModel::id) { item ->
			CharacterListElement(item, itemModifier.clickable {
				characterListViewModel.onCharacterSelected(item)
			})
		}
		item {
			Card(elevation = 8.dp, modifier = itemModifier.clickable {
				characterListViewModel.onAddNewPressed()
			}) {
				Text("Add new", modifier = Modifier
					.padding(defaultPadding)
					.fillMaxWidth(), textAlign = TextAlign.Center)
			}
		}
	}
}


@Composable
fun CharacterListElement(characterViewModel: CharacterViewModel, modifier: Modifier = Modifier) {
	Card(elevation = 8.dp, modifier = modifier) {
		Text(text = characterViewModel.name, modifier = Modifier.padding(defaultPadding))
	}
}

@Preview(device = Devices.NEXUS_5, showBackground = true)
@Composable
fun PreviewCharacterListView() {
	CharacterListView(MockCharacterListViewModel())
}

private class MockCharacterListViewModel : CharacterListViewModel {
	override val characters: StateFlow<List<CharacterViewModel>>
		get() = MutableStateFlow(
			listOf(
				CharacterViewModel(1, "Test Character")
			)
		)

	override fun onCharacterSelected(characterViewModel: CharacterViewModel) {
		TODO("Not yet implemented")
	}

	override fun onAddNewPressed() {
		TODO("Not yet implemented")
	}

}
