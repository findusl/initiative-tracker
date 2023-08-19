package de.lehrbaum.initiativetracker.ui.character

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.lehrbaum.initiativetracker.ui.Constants

@Composable
fun CharacterListElement(characterViewModel: CharacterViewModel, modifier: Modifier = Modifier) {
    Card(
		elevation = 8.dp,
		modifier = modifier
			.padding(Constants.defaultPadding)
			.fillMaxWidth()
	) {
        Text(text = characterViewModel.name, modifier = Modifier.padding(Constants.defaultPadding))
    }
}