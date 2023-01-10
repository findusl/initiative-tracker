package de.lehrbaum.initiativetracker.view.combat.characters

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun CharacterListView(
	characterListViewModel: CharacterListViewModel,
	modifier: Modifier = Modifier,
) {
	val charactersState by characterListViewModel.characters.collectAsStateWithLifecycle()

	LazyColumn(modifier) {
		items(charactersState, key = CharacterViewModel::id) { item ->
			Card {
				Text(item.name)
			}
		}
		item {
			Card {
				Text("Add new")
			}
		}
	}
}

@Preview
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

}
