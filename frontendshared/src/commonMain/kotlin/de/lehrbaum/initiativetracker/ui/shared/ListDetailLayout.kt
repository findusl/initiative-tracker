package de.lehrbaum.initiativetracker.ui.shared

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier

val LocalWidescreen = compositionLocalOf { false }

@Composable
fun ProvideScreenSizeInformation(width: Int, height: Int, content: @Composable () -> Unit) {
	val isWidescreen = width > 840 && height > 480
	CompositionLocalProvider(LocalWidescreen provides isWidescreen, content = content)
}

@Composable
fun ListDetailLayout(list: @Composable () -> Unit, detail: @Composable (() -> Unit)?) {
	val isWidescreen = LocalWidescreen.current
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
			detail()
		} else {
			list()
		}
	}
}
