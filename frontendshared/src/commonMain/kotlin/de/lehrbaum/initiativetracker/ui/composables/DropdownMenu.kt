package de.lehrbaum.initiativetracker.ui.composables

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties

@Composable
fun DropdownMenu(
	expanded: Boolean,
	onDismissRequest: () -> Unit = {},
	focusable: Boolean = true,
	modifier: Modifier = Modifier,
	offset: DpOffset = DpOffset(0.dp, 0.dp),
	content: @Composable ColumnScope.() -> Unit
) {
	androidx.compose.material.DropdownMenu(
		expanded,
		onDismissRequest,
		modifier,
		offset,
		properties = PopupProperties(focusable),
		content = content
	)
}
