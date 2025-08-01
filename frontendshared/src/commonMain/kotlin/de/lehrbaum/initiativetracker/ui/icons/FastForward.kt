package de.lehrbaum.initiativetracker.ui.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

@Suppress("UnusedReceiverParameter") // Standard for icons
val Icons.Filled.FastForward: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
	materialIcon(name = "Filled.FastForward") {
		materialPath {
			moveTo(4f, 18f)
			lineTo(12.5f, 12f)
			lineTo(4f, 6f)
			verticalLineToRelative(12f)
			close()
			moveTo(13f, 6f)
			lineTo(21.5f, 12f)
			lineTo(13f, 18f)
			verticalLineToRelative(-12f)
			close()
		}
	}
}
