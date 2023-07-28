package de.lehrbaum.initiativetracker.ui.composables

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.material.DropdownMenu as DesktopDropdownMenu

@Composable
actual fun DropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    focusable: Boolean,
    modifier: Modifier,
    offset: DpOffset,
    content: @Composable ColumnScope.() -> Unit
) {
	DesktopDropdownMenu(expanded, onDismissRequest, focusable, modifier, offset, content)
}