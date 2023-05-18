package de.lehrbaum.initiativetracker.ui.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import de.lehrbaum.initiativetracker.ui.Constants
import de.lehrbaum.initiativetracker.ui.shared.CombatantViewModel

@Composable
fun CombatantListElement(combatant: CombatantViewModel, modifier: Modifier = Modifier) {
	val outerBackgroundColor by animateColorAsState(
		if (combatant.active) MaterialTheme.colors.secondary else MaterialTheme.colors.background
	)
	val innerBackgroundColor = combatant.healthPercentage.healthToBrush()
	Box(modifier = Modifier.background(outerBackgroundColor)) {
		Card(elevation = 8.dp, modifier = modifier) {
			Row(Modifier.background(innerBackgroundColor)){
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

private data class HealthColors(
	val alpha: Float
) {
	val backgroundGreen = Color.Green.copy(alpha = alpha)
	val backgroundRed = Color.Red.copy(alpha = alpha)
}

@Composable
private fun Double.healthToBrush(colors: HealthColors = HealthColors(ContentAlpha.medium)): Brush {
	return when {
		this > 0.99 -> SolidColor(colors.backgroundGreen)
		this > 0.75 -> Brush.horizontalGradient(0.75f to colors.backgroundGreen, 1.0f to colors.backgroundRed)
		this > 0.37 -> Brush.horizontalGradient(0.37f to colors.backgroundGreen, 0.75f to colors.backgroundRed)
		this > 0.0 -> Brush.horizontalGradient(0.0f to colors.backgroundGreen, 0.37f to colors.backgroundRed)
		this <= 0.0 -> SolidColor(colors.backgroundRed)
		else -> SolidColor(MaterialTheme.colors.background)
	}
}
