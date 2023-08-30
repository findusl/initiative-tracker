package de.lehrbaum.initiativetracker.ui.character

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import de.lehrbaum.initiativetracker.ui.composables.*
import de.lehrbaum.initiativetracker.ui.shared.ListDetailLayout

@Composable
@ExperimentalMaterialApi
@ExperimentalFoundationApi
fun CharacterListScreen(drawerState: DrawerState, characterListViewModel: CharacterListViewModel) {
	val characters by characterListViewModel.characters.collectAsState(emptyList())

	ListDetailLayout(
		list = {
			Scaffold(
				topBar = { TopBar(drawerState) }
			) {
				CharacterList(
					characters,
					characterListViewModel::editCharacter,
					characterListViewModel::addNewCharacter,
					dismissToStartAction = swipeToDelete(characterListViewModel::deleteCharacter)
				)
			}
		},
		detail = characterListViewModel.editCharacterModel.value?.let {
			{ EditCharacterScreen(it) }
		},
		onDetailDismissRequest = {
			characterListViewModel.editCharacterModel.value?.cancel()
		}
	)
}

@Composable
@ExperimentalMaterialApi
@ExperimentalFoundationApi
private fun CharacterList(
	characters: List<CharacterViewModel>,
	onCharacterSelected: (CharacterViewModel) -> Unit,
	onAddNewPressed: () -> Unit,
	dismissToEndAction: SwipeToDismissAction<CharacterViewModel>? = null,
	dismissToStartAction: SwipeToDismissAction<CharacterViewModel>? = null,
) {
	LazyColumn {
		items(characters, key = CharacterViewModel::id) { item ->
			SwipeToDismiss(dismissToEndAction, dismissToStartAction, item) {
				CharacterListElement(item, Modifier.clickable {
					onCharacterSelected(item)
				})
			}
		}
		addCreateNewCard("Add new character", onAddNewPressed)
	}
}

@Composable
private fun TopBar(
	drawerState: DrawerState,
) {
	TopAppBar(
		title = {
			Text("Manage Characters", color = MaterialTheme.colors.onPrimary)
		},
		navigationIcon = {
			BurgerMenuButtonForDrawer(drawerState)
		},
		backgroundColor = MaterialTheme.colors.primarySurface,
	)
}
