package de.lehrbaum.initiativetracker.ui.screen.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.lehrbaum.initiativetracker.ui.model.shared.CombatantViewModel
import de.lehrbaum.initiativetracker.ui.screen.Constants


@Composable
fun CombatantListElement(combatant: CombatantViewModel, modifier: Modifier = Modifier) {
	val backgroundColor by animateColorAsState(
		if (combatant.active) MaterialTheme.colors.secondary else MaterialTheme.colors.background
	)
	Box(modifier = Modifier.background(backgroundColor)) {
		Card(elevation = 8.dp, modifier = modifier) {
			Row {
				Text(
					text = combatant.name, modifier = Modifier
						.padding(Constants.defaultPadding)
						.weight(1.0f, fill = true)
				)
				Text(text = combatant.initiativeString, modifier = Modifier.padding(Constants.defaultPadding))
			}
		}
	}
}
