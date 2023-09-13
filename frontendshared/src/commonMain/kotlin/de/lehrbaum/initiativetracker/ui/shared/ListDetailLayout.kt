package de.lehrbaum.initiativetracker.ui.shared

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ListDetailLayout(
	list: @Composable () -> Unit,
	detail: @Composable (() -> Unit)?,
	onDetailDismissRequest: (() -> Unit)? = null,
) {
	BoxWithConstraints {
		val isWidescreen = maxWidth.value > 840 && maxHeight.value > 480
		if (isWidescreen) {
			Row(
				horizontalArrangement = Arrangement.SpaceEvenly,
				modifier = Modifier.fillMaxWidth()
			) {
				val modifier = Modifier.fillMaxHeight().fillMaxWidth().weight(1f)
				Box(modifier = modifier) {
					list()
				}
				detail?.let {
					Box(modifier) {
						detail()
					}
				}
			}
		} else {
			if (detail != null) {
				BackHandler {
					onDetailDismissRequest?.let { it() }
				}
				detail()
			} else {
				list()
			}
		}
	}
}
