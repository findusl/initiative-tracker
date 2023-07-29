package de.lehrbaum.initiativetracker.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.width
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity

@Composable
fun AutocompleteTextField(
    modifier: Modifier = Modifier,
    text: String,
    label: String,
    onTextChanged: (String) -> Unit,
    error: Boolean,
    suggestions: List<String> = emptyList(),
    placeholder: String? = null,
) {
	var expanded by remember { mutableStateOf(false) }
	var textFieldWidth by remember { mutableStateOf(0) }
	var textFieldHeight by remember { mutableStateOf(0) }

    Column(modifier) {
        OutlinedTextField(
            value = text,
            onValueChange = onTextChanged,
            modifier = Modifier.fillMaxWidth()
                .onSizeChanged {
                    textFieldWidth = it.width
					textFieldHeight = it.height
                }.onFocusChanged {
                    expanded = it.isFocused
                },
            label = { Text(label) },
            isError = error && !expanded,
            placeholder = placeholder?.let { { Text(it) } },
			trailingIcon = {
				if (expanded) {
					IconButton(onClick = { expanded = false }) {
						Icon(
							Icons.Filled.ArrowDropDown,
							contentDescription = "Close Dropdown",
							modifier = Modifier.graphicsLayer(scaleY = -1f)
						)
					}
				}
			},
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            focusable = false,
            modifier = Modifier
                .width(with(LocalDensity.current) { textFieldWidth.toDp() })
				.requiredHeightIn(max = with(LocalDensity.current) { (5*textFieldHeight).toDp() }),
        ) {
            suggestions.forEach { label ->
                DropdownMenuItem(onClick = {
					onTextChanged(label)
					expanded = false
				}) {
                    Text(text = label)
                }
            }
        }
    }
}
