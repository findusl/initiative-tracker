package de.lehrbaum.initiativetracker.ui.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Card
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import de.lehrbaum.initiativetracker.ui.Constants.defaultPadding
import de.lehrbaum.initiativetracker.ui.shared.CombatantViewModel
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@Composable
fun CombatantListElement(combatant: CombatantViewModel, isHost: Boolean, modifier: Modifier = Modifier) {
	val outerBackgroundColor by animateColorAsState(
		if (combatant.active) MaterialTheme.colorScheme.secondary else Color.Transparent
	)
	var disabled by remember { mutableStateOf(combatant.disabled) }
	disabled = combatant.disabled
	val crossRed = Color.Red.copy(alpha = 0.38f)
	val getsAllInformation = combatant.isOwned || isHost

	val innerBackgroundColor = combatant.healthPercentage.healthToBrush(enabled = !disabled)
	Card(
		modifier = modifier
			.background(outerBackgroundColor)
			.padding(defaultPadding)
	) {
		ListItem(
			modifier = Modifier
				.background(innerBackgroundColor)
				.drawBehind {
					if (disabled) {
						drawDisabledCross(crossRed)
					}
				},
			leadingContent = {
			   if (!combatant.isHidden || getsAllInformation) {
				   combatant.imageUrl()?.let {
					   KamelImage(
						   asyncPainterResource(it),
						   contentDescription = "Icon of ${combatant.name}",
						   contentAlignment = Alignment.CenterStart,
						   modifier = Modifier
							   .widthIn(min = 20.dp, max = 40.dp) // guessed the values
							   .aspectRatio(ratio = 1.0f)
					   )
				   }
			   }
			},
			supportingContent = {
				if (getsAllInformation) {
					combatant.monsterDTO?.let { monster ->
						val informationItems = listOf(
							monster.ac?.firstOrNull()?.ac?.let { "AC $it" },
							"${combatant.currentHp}/${combatant.maxHp}"
						)
						Text(informationItems.joinToString(", "))
					}
				}
			},
			headlineContent = {
				if (combatant.isHidden && !getsAllInformation)
					Text("<Hidden>")
				else
					Text(text = combatant.name)
			},
			trailingContent = {
				Text(text = combatant.initiativeString)
			}
		)
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
private fun Double?.healthToBrush(
	enabled: Boolean,
	colors: HealthColors = HealthColors(if (enabled) 0.6f else 0.38f)
): Brush {
	return when {
		this == null -> SolidColor(MaterialTheme.colorScheme.background)
		this > 0.99 -> SolidColor(colors.backgroundGreen)
		this > 0.75 -> Brush.horizontalGradient(0.75f to colors.backgroundGreen, 1.0f to colors.backgroundRed)
		this > 0.37 -> Brush.horizontalGradient(0.37f to colors.backgroundGreen, 0.75f to colors.backgroundRed)
		this > 0.0 -> Brush.horizontalGradient(0.0f to colors.backgroundGreen, 0.37f to colors.backgroundRed)
		this <= 0.0 -> SolidColor(colors.backgroundRed)
		else -> SolidColor(MaterialTheme.colorScheme.background)
	}
}
