package de.lehrbaum.initiativetracker.ui.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissState
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Place
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import de.lehrbaum.initiativetracker.ui.shared.DarkGreen
import de.lehrbaum.initiativetracker.ui.shared.SwipeResponse

@Composable
@ExperimentalMaterialApi
fun <E> SwipeToDismiss(
	dismissToEndAction: SwipeToDismissAction<E>? = null,
	dismissToStartAction: SwipeToDismissAction<E>? = null,
	element: E,
	content: @Composable () -> Unit,
) {
	if (dismissToEndAction == null && dismissToStartAction == null) {
		// fast path
		content()
		return
	}

	val dismissState = remember(dismissToEndAction?.action, dismissToStartAction?.action) {
		DismissState(DismissValue.Default) {
			when (it) {
				DismissValue.DismissedToEnd -> dismissToEndAction?.action?.invoke(element)

				DismissValue.DismissedToStart -> dismissToStartAction?.action?.invoke(element)

				DismissValue.Default -> null // Happens when the user lets go before finishing the slide
			}?.shouldSlideOut ?: false
		}
	}

	val directions = setOfNotNull(
		dismissToEndAction?.let { DismissDirection.StartToEnd },
		dismissToStartAction?.let { DismissDirection.EndToStart },
	)

	val backgroundLambda = remember<@Composable RowScope.() -> Unit>(dismissToEndAction, dismissToStartAction) {
		{ SwipeToDismissBackground(dismissToEndAction, dismissToStartAction, dismissState) }
	}

	SwipeToDismiss(
		state = dismissState,
		directions = directions,
		dismissThresholds = { FractionalThreshold(0.3f) },
		background = backgroundLambda,
		dismissContent = { content() },
	)
}

@Composable
fun <E : Any> swipeToDelete(delete: (E) -> Unit): SwipeToDismissAction<E> =
	SwipeToDismissAction(
		MaterialTheme.colors.error,
		Icons.Default.Delete,
		contentDescription = "Delete List element",
		action = { element ->
			delete(element)
			SwipeResponse.SLIDE_OUT
		},
	)

fun <E : Any> swipeToDisable(disable: (E) -> Unit): SwipeToDismissAction<E> =
	SwipeToDismissAction(
		Color.DarkGray,
		Icons.Default.Close,
		contentDescription = "Disable List element",
		action = { element ->
			disable(element)
			SwipeResponse.SLIDE_BACK
		},
	)

@Composable
fun <E : Any> swipeToEnable(enable: (E) -> Unit): SwipeToDismissAction<E> =
	SwipeToDismissAction(
		Color.DarkGreen,
		Icons.Default.Check,
		contentDescription = "Enable List element",
		action = { element ->
			enable(element)
			SwipeResponse.SLIDE_BACK
		},
	)

@Composable
fun <E : Any> swipeToJumpToTurn(jumpToTurn: (E) -> Unit): SwipeToDismissAction<E> =
	SwipeToDismissAction(
		MaterialTheme.colors.secondary,
		Icons.Default.Place,
		contentDescription = "Set list element active",
		action = { element ->
			jumpToTurn(element)
			SwipeResponse.SLIDE_BACK
		},
	)

@Immutable
data class SwipeToDismissAction<E>(
	val color: Color,
	val icon: ImageVector,
	val contentDescription: String,
	val action: (element: E) -> SwipeResponse,
)

@Composable
@ExperimentalMaterialApi
private fun SwipeToDismissBackground(
	dismissToEndAction: SwipeToDismissAction<*>?,
	dismissToStartAction: SwipeToDismissAction<*>?,
	dismissState: DismissState,
) {
	val direction = dismissState.dismissDirection ?: return

	val color by animateColorAsState(
		when (dismissState.targetValue) {
			DismissValue.Default -> Color.LightGray
			DismissValue.DismissedToEnd -> dismissToEndAction?.color
			DismissValue.DismissedToStart -> dismissToStartAction?.color
		} ?: return,
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
		if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f,
	)

	Box(
		Modifier
			.fillMaxSize()
			// Could use drawBehind here to optimize https://stackoverflow.com/questions/74361197/jetpack-compose-avoid-unnecessary-recomposition
			.background(color)
			.padding(horizontal = 20.dp),
		contentAlignment = alignment,
	) {
		Icon(
			icon,
			contentDescription = description,
			modifier = Modifier.scale(scale),
		)
	}
}
