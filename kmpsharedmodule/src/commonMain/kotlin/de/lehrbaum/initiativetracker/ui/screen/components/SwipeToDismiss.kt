package de.lehrbaum.initiativetracker.ui.screen.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import de.lehrbaum.initiativetracker.ui.model.SwipeResponse
import io.github.aakira.napier.Napier

@Composable
@ExperimentalMaterialApi
fun <E> SwipeToDismiss(
	dismissToEndAction: SwipeToDismissAction<E>? = null,
	dismissToStartAction: SwipeToDismissAction<E>? = null,
	element: E,
	content: @Composable () -> Unit
) {
	if (dismissToEndAction == null && dismissToStartAction == null) {
		// fast path
		content()
		return
	}

	val dismissState = rememberDismissState {
		when (it) {
			DismissValue.DismissedToEnd -> dismissToEndAction?.action?.invoke(element)

			DismissValue.DismissedToStart -> dismissToStartAction?.action?.invoke(element)

			else -> {
				Napier.w { "Dismissed to unknown state $it" }
				null
			}
		}?.dismissResponse ?: false
	}

	val directions = setOfNotNull(
		dismissToEndAction?.let { DismissDirection.StartToEnd },
		dismissToStartAction?.let { DismissDirection.EndToStart }
	)

	SwipeToDismiss(
		state = dismissState,
		directions = directions,
		dismissThresholds = { FractionalThreshold(0.3f) },
		background = { SwipeToDismissBackground(dismissToEndAction, dismissToStartAction, dismissState) },
		dismissContent = { content() }
	)
}

@Immutable
data class SwipeToDismissAction<E>(
	val color: Color,
	val icon: ImageVector,
	val contentDescription: String,
	val action: (element: E) -> SwipeResponse
)

@Composable
@ExperimentalMaterialApi
private fun SwipeToDismissBackground(
	dismissToEndAction: SwipeToDismissAction<*>?,
	dismissToStartAction: SwipeToDismissAction<*>?,
	dismissState: DismissState
) {
	val direction = dismissState.dismissDirection ?: return

	val color by animateColorAsState(
		when (dismissState.targetValue) {
			DismissValue.Default -> Color.LightGray
			DismissValue.DismissedToEnd -> dismissToEndAction?.color
			DismissValue.DismissedToStart -> dismissToStartAction?.color
		} ?: return
	)

	val alignment = when (direction) {
		DismissDirection.StartToEnd -> Alignment.CenterStart
		DismissDirection.EndToStart -> Alignment.CenterEnd
	}
	val icon = when (direction) {
		DismissDirection.StartToEnd -> dismissToEndAction?.icon
		DismissDirection.EndToStart -> dismissToStartAction?.icon
	} ?: return

	val description = when (direction) {
		DismissDirection.StartToEnd -> dismissToEndAction?.contentDescription
		DismissDirection.EndToStart -> dismissToStartAction?.contentDescription
	}

	val scale by animateFloatAsState(
		if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f
	)

	Box(
		Modifier
			.fillMaxSize()
			.background(color)
			.padding(horizontal = 20.dp),
		contentAlignment = alignment
	) {
		Icon(
			icon,
			contentDescription = description,
			modifier = Modifier.scale(scale)
		)
	}
}
