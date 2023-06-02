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
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import de.lehrbaum.initiativetracker.ui.Constants
import de.lehrbaum.initiativetracker.ui.shared.CombatantViewModel

@Composable
fun CombatantListElement(combatant: CombatantViewModel, modifier: Modifier = Modifier) {
	val outerBackgroundColor by animateColorAsState(
		if (combatant.active) MaterialTheme.colors.secondary else MaterialTheme.colors.background
	)
	var disabled by remember { mutableStateOf(combatant.disabled) }
	disabled = combatant.disabled
	val crossRed = Color.Red.copy(alpha = ContentAlpha.disabled)

	val innerBackgroundColor = combatant.healthPercentage.healthToBrush(enabled = !disabled)
	Box(modifier = Modifier.background(outerBackgroundColor)) {
		Card(elevation = 8.dp, modifier = modifier) {
			Row(Modifier
				.background(innerBackgroundColor)
				.drawBehind {
					if (disabled) {
						drawDisabledCross(crossRed)
					}
				}
			){
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

private fun DrawScope.drawDisabledCross(color: Color) {
	val strokeWidth = 5.dp.toPx()

	// Draw the diagonal lines of the St. Andrew's cross
	drawLine(
		color = color,
		start = Offset(0f, 0f),
		end = Offset(size.width, size.height),
		strokeWidth = strokeWidth
	)
	drawLine(
		color = color,
		start = Offset(0f, size.height),
		end = Offset(size.width, 0f),
		strokeWidth = strokeWidth
	)
}

private data class HealthColors(
	val alpha: Float
) {
	val backgroundGreen = Color.Green.copy(alpha = alpha)
	val backgroundRed = Color.Red.copy(alpha = alpha)
}

@Composable
private fun Double.healthToBrush(
	enabled: Boolean,
	colors: HealthColors = HealthColors(if (enabled) ContentAlpha.medium else ContentAlpha.disabled)
): Brush {
	return when {
		this > 0.99 -> SolidColor(colors.backgroundGreen)
		this > 0.75 -> Brush.horizontalGradient(0.75f to colors.backgroundGreen, 1.0f to colors.backgroundRed)
		this > 0.37 -> Brush.horizontalGradient(0.37f to colors.backgroundGreen, 0.75f to colors.backgroundRed)
		this > 0.0 -> Brush.horizontalGradient(0.0f to colors.backgroundGreen, 0.37f to colors.backgroundRed)
		this <= 0.0 -> SolidColor(colors.backgroundRed)
		else -> SolidColor(MaterialTheme.colors.background)
	}
}
