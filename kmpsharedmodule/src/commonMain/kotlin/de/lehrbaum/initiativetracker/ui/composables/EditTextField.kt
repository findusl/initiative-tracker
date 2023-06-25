package de.lehrbaum.initiativetracker.ui.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import de.lehrbaum.initiativetracker.ui.shared.EditFieldViewModel
import io.ktor.util.reflect.TypeInfo
import io.ktor.util.reflect.typeInfo

@Composable
fun <T> EditTextField(
	editFieldViewModel: EditFieldViewModel<T>,
	label: String,
	keyboardOptions: KeyboardOptions,
	modifier: Modifier = Modifier,
) {
	var selectAllNextFocus by remember(editFieldViewModel) { mutableStateOf(editFieldViewModel.selectOnFirstFocus) }
	var selectWhole by remember { mutableStateOf(false) } // https://stackoverflow.com/a/70241741/3795043
	var textFieldValue by remember(editFieldViewModel) {
		mutableStateOf(TextFieldValue(editFieldViewModel.initialValueText))
	}
	var showClearButton by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = textFieldValue,
        onValueChange = {
			if (selectWhole && it.text.isNotEmpty()) {
				selectWhole = false
				textFieldValue = it.copy(selection = TextRange(0, it.text.length))
			} else {
				textFieldValue = it
			}
			editFieldViewModel.currentState = it.text
		},
        label = { Text(label) },
		placeholder = editFieldViewModel.placeholder?.let { { Text(it) } },
        isError = editFieldViewModel.hasError,
        keyboardOptions = keyboardOptions,
        singleLine = editFieldViewModel.singleLine,
		trailingIcon = {
			if (showClearButton && textFieldValue.text.isNotEmpty()) {
				IconButton(onClick = { textFieldValue = TextFieldValue() }) {
					Icon(imageVector = Icons.Filled.Close, contentDescription = "Clear")
				}
			}
		},
        modifier = modifier
			.fillMaxWidth()
			.onFocusChanged {
				showClearButton = it.hasFocus
				if (selectAllNextFocus && it.hasFocus) {
					selectAllNextFocus = false
					selectWhole = true
				}
			}
    )
}

@Composable
inline fun <reified T> EditTextField(editFieldViewModel: EditFieldViewModel<T>, label: String, modifier: Modifier = Modifier) {
	EditTextField(editFieldViewModel, label, guessKeyboardOptions(editFieldViewModel), modifier)
}

inline fun <reified T> guessKeyboardOptions(editFieldViewModel: EditFieldViewModel<T>): KeyboardOptions {
	return guessKeyboardOptions(editFieldViewModel, typeInfo<T>())
}

fun guessKeyboardOptions(editFieldViewModel: EditFieldViewModel<*>, typeInfo: TypeInfo): KeyboardOptions {
	var type = editFieldViewModel.keyboardType

	if (type == null) {
		type = when(typeInfo.type) {
			Int::class, Long::class -> KeyboardType.Number
			Double::class, Float::class -> KeyboardType.Decimal
			else -> null
		}
	}

	return if (type != null) KeyboardOptions(keyboardType = type) else KeyboardOptions.Default
}

