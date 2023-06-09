package de.lehrbaum.initiativetracker.ui.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import de.lehrbaum.initiativetracker.ui.shared.EditField
import kotlin.reflect.KClass

@Composable
fun <T> EditTextField(
	editField: EditField<T>,
	label: String,
	keyboardOptions: KeyboardOptions,
	modifier: Modifier = Modifier,
) {
	var selectAllNextFocus by remember(editField) { mutableStateOf(editField.selectOnFirstFocus) }
	var selectWhole by remember { mutableStateOf(false) } // https://stackoverflow.com/a/70241741/3795043
	var textFieldValue by remember(editField) {
		mutableStateOf(TextFieldValue(editField.initialValueText))
	}

    OutlinedTextField(
        value = textFieldValue,
        onValueChange = {
			if (selectWhole) {
				selectWhole = false
				textFieldValue = it.copy(selection = TextRange(0, it.text.length))
			} else {
				textFieldValue = it
			}
			editField.currentState = it.text
		},
        label = { Text(label) },
		placeholder = editField.placeholder?.let { { Text(it) } },
        isError = editField.hasError,
        keyboardOptions = keyboardOptions,
        singleLine = editField.singleLine,
        modifier = modifier
			.fillMaxWidth()
			.onFocusChanged {
				if (selectAllNextFocus && it.hasFocus) {
					selectAllNextFocus = false
					selectWhole = true
				}
			}
    )
}

@Composable
inline fun <reified T> EditTextField(editField: EditField<T>, label: String, modifier: Modifier = Modifier) {
	EditTextField(editField, label, guessKeyboardOptions(editField), modifier)
}

inline fun <reified T> guessKeyboardOptions(editField: EditField<T>): KeyboardOptions {
	@Suppress("UNCHECKED_CAST") // Hacky workaround. But it seems to run
	return guessKeyboardOptions(editField, T::class as KClass<Any>)
}

fun <T: Any> guessKeyboardOptions(editField: EditField<*>, clazz: KClass<T>): KeyboardOptions {
	var type = editField.keyboardType

	if (type == null) {
		type = when(clazz) {
			Int::class, Long::class -> KeyboardType.Number
			Double::class, Float::class -> KeyboardType.Decimal
			else -> null
		}
	}

	return if (type != null) KeyboardOptions(keyboardType = type) else KeyboardOptions.Default
}

