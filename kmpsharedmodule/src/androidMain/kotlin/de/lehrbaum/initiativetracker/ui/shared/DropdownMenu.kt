package de.lehrbaum.initiativetracker.ui.shared

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.window.PopupProperties
import androidx.compose.material.DropdownMenu as ActualDropdownMenu

@Composable
actual fun DropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    focusable: Boolean,
    modifier: Modifier,
    offset: DpOffset,
    content: @Composable ColumnScope.() -> Unit
) {
	ActualDropdownMenu(expanded, onDismissRequest, modifier, offset, PopupProperties(focusable),content)
}