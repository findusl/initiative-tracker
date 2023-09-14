package de.lehrbaum.initiativetracker.ui.character

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.lehrbaum.initiativetracker.ui.Constants

@Composable
fun CharacterListElement(characterViewModel: CharacterViewModel, modifier: Modifier = Modifier) {
    Card(
		modifier = modifier
			.padding(Constants.defaultPadding)
			.fillMaxWidth()
	) {
        Text(text = characterViewModel.name, modifier = Modifier.padding(Constants.defaultPadding))
    }
}