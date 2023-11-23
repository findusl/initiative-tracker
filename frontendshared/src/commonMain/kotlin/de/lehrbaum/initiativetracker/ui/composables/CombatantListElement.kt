package de.lehrbaum.initiativetracker.ui.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
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
import de.lehrbaum.initiativetracker.ui.shared.DarkGreen
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CombatantListElement(combatantVM: CombatantListViewModel, isHost: Boolean, modifier: Modifier = Modifier) {
	val combatant = combatantVM.combatant
	val outerBackgroundColor by animateColorAsState(
		if (combatantVM.active) MaterialTheme.colors.secondary else Color.Transparent
	)
	var disabled by remember { mutableStateOf(combatantVM.disabled) }
	disabled = combatantVM.disabled
	val crossRed = Color.Red.copy(alpha = ContentAlpha.disabled)
	val getsAllInformation = combatantVM.isOwned || isHost

	val innerBackgroundColor = combatantVM.healthPercentage.healthToBrush(enabled = !disabled)
	Card(
		elevation = 8.dp,
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
			icon = composableIf(!combatant.isHidden || getsAllInformation) {
				combatantVM.imageUrl()?.let {
					KamelImage(
						asyncPainterResource(it),
						contentDescription = "Icon of ${combatant.name}",
						contentAlignment = Alignment.CenterStart,
						modifier = Modifier
							.widthIn(min = 20.dp, max = 40.dp) // guessed the values
							.aspectRatio(ratio = 1.0f)
					)
				}
			},
			secondaryText = composableIf(getsAllInformation) {
				val informationItems = listOfNotNull(
					combatantVM.monsterDTO?.ac?.firstOrNull()?.ac?.let { "AC $it" },
					"${combatant.currentHp}/${combatant.maxHp}"
				)
				Text(informationItems.joinToString(", "))
			},
			text = {
				if (combatant.isHidden && !getsAllInformation)
					Text("<Hidden>")
				else
					Text(text = combatant.name)
			},
			trailing = {
				Text(text = combatantVM.initiativeString)
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
	val backgroundGreen = Color.DarkGreen.copy(alpha = alpha)
	val backgroundRed = Color.Red.copy(alpha = alpha)
}

@Composable
private fun Double?.healthToBrush(
	enabled: Boolean,
	colors: HealthColors = HealthColors(if (enabled) ContentAlpha.medium else ContentAlpha.disabled)
): Brush {
	return when {
		this == null -> SolidColor(MaterialTheme.colors.background)
		this > 0.99 -> SolidColor(colors.backgroundGreen)
		this > 0.75 -> Brush.horizontalGradient(0.75f to colors.backgroundGreen, 1.0f to colors.backgroundRed)
		this > 0.37 -> Brush.horizontalGradient(0.37f to colors.backgroundGreen, 0.75f to colors.backgroundRed)
		this > 0.0 -> Brush.horizontalGradient(0.0f to colors.backgroundGreen, 0.37f to colors.backgroundRed)
		this <= 0.0 -> SolidColor(colors.backgroundRed)
		else -> SolidColor(MaterialTheme.colors.background)
	}
}
