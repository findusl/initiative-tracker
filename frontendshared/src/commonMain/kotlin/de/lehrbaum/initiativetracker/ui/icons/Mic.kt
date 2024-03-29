package de.lehrbaum.initiativetracker.ui.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

@Suppress("UnusedReceiverParameter") // Standard for icons
val Icons.Filled.Mic: ImageVector
	get() {
		if (_mic != null) {
			return _mic!!
		}
		_mic = materialIcon(name = "Filled.Mic") {
			materialPath {
				moveTo(12.0f, 14.0f)
				curveToRelative(1.66f, 0.0f, 2.99f, -1.34f, 2.99f, -3.0f)
				lineTo(15.0f, 5.0f)
				curveToRelative(0.0f, -1.66f, -1.34f, -3.0f, -3.0f, -3.0f)
				reflectiveCurveTo(9.0f, 3.34f, 9.0f, 5.0f)
				verticalLineToRelative(6.0f)
				curveToRelative(0.0f, 1.66f, 1.34f, 3.0f, 3.0f, 3.0f)
				close()
				moveTo(17.3f, 11.0f)
				curveToRelative(0.0f, 3.0f, -2.54f, 5.1f, -5.3f, 5.1f)
				reflectiveCurveTo(6.7f, 14.0f, 6.7f, 11.0f)
				lineTo(5.0f, 11.0f)
				curveToRelative(0.0f, 3.41f, 2.72f, 6.23f, 6.0f, 6.72f)
				lineTo(11.0f, 21.0f)
				horizontalLineToRelative(2.0f)
				verticalLineToRelative(-3.28f)
				curveToRelative(3.28f, -0.48f, 6.0f, -3.3f, 6.0f, -6.72f)
				horizontalLineToRelative(-1.7f)
				close()
			}
		}
		return _mic!!
	}

private var _mic: ImageVector? = null
