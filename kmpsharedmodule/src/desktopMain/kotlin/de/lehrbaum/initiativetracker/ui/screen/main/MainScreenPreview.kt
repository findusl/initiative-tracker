package de.lehrbaum.initiativetracker.ui.screen.main

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable

@Preview
@Composable
fun MainScreenPreview() {
	IconButton(onClick = {}) {
		Icon(Icons.Default.Close, contentDescription = "Leave combat")
	}
}
