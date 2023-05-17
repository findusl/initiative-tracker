package de.lehrbaum.initiativetracker.ui.character

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
import de.lehrbaum.initiativetracker.ui.Constants
import de.lehrbaum.initiativetracker.ui.composables.BurgerMenuButtonForDrawer
import de.lehrbaum.initiativetracker.ui.composables.SwipeToDismissAction
import de.lehrbaum.initiativetracker.ui.composables.addCreateNewCard
import de.lehrbaum.initiativetracker.ui.composables.swipeToDeleteAction

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
			de.lehrbaum.initiativetracker.ui.composables.SwipeToDismiss(dismissToEndAction, dismissToStartAction, item) {
				CharacterListElement(item, itemModifier.clickable {
					onCharacterSelected(item)
				})
			}
		}
		addCreateNewCard(itemModifier, "Add new character", onAddNewPressed)
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
