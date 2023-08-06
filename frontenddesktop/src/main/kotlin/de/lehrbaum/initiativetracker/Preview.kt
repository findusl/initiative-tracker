package de.lehrbaum.initiativetracker

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor

@Composable
@Preview
fun TestStuff() {
	Box(modifier = Modifier
		.background(0.0.healthToBrush())
		.fillMaxSize()
	) {
		Text("Some text")
	}
}

private val backgroundGreen = Color.Green.copy(alpha = 0.5f)
private val backgroundRed = Color.Red.copy(alpha = 0.5f)

@Composable
private fun Double.healthToBrush(): Brush {
	return when {
		this > 0.99 -> SolidColor(backgroundGreen)
		this > 0.75 -> Brush.horizontalGradient(0.75f to backgroundGreen, 1.0f to backgroundRed)
		this > 0.37 -> Brush.horizontalGradient(0.37f to backgroundGreen, 0.75f to backgroundRed)
		this > 0.0 -> Brush.horizontalGradient(0.0f to backgroundGreen, 0.37f to backgroundRed)
		this <= 0.0 -> SolidColor(backgroundRed)
		else -> SolidColor(MaterialTheme.colors.background)
	}
}
