package de.lehrbaum.initiativetracker.ui.screen.character

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.lehrbaum.initiativetracker.ui.model.character.CharacterListModel
import de.lehrbaum.initiativetracker.ui.model.character.CharacterViewModel
import de.lehrbaum.initiativetracker.ui.screen.Constants
import de.lehrbaum.initiativetracker.ui.screen.components.*

@Composable
@ExperimentalMaterialApi
@ExperimentalFoundationApi
fun CharacterListScreen(drawerState: DrawerState, characterListModel: CharacterListModel) {
	val characters by characterListModel.characters.collectAsState(emptyList())
	Scaffold(
		topBar = { TopBar(drawerState) }
	) {
		CharacterList(
			characters,
			characterListModel::editCharacter,
			characterListModel::addNewCharacter,
			dismissToStartAction = swipeToDeleteAction(characterListModel::deleteCharacter)
		)
	}

	characterListModel.editCharacterModel.value?.let {
		EditCharacterDialog(it)
	}
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
		val itemModifier = Modifier
			.padding(Constants.defaultPadding)
			.fillMaxWidth()
		items(characters, key = CharacterViewModel::id) { item ->
			SwipeToDismiss(dismissToEndAction, dismissToStartAction, item) {
				CharacterListElement(item, itemModifier.clickable {
					onCharacterSelected(item)
				})
			}
		}
		addCreateNewCard(itemModifier, "Add new character", onAddNewPressed)
	}
}

@Composable
fun CharacterListElement(characterViewModel: CharacterViewModel, modifier: Modifier = Modifier) {
	Card(elevation = 8.dp, modifier = modifier) {
		Text(text = characterViewModel.name, modifier = Modifier.padding(Constants.defaultPadding))
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
