package de.lehrbaum.initiativetracker.ui.screen.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import de.lehrbaum.initiativetracker.ui.model.shared.EditField

@Composable
inline fun <reified T> EditTextField(editField: EditField<T>, label: String) {
    OutlinedTextField(
        value = editField.currentState,
        onValueChange = { editField.currentState = it },
        label = { Text(label) },
        isError = editField.hasError,
        keyboardOptions = guessKeyboardOptions(editField),
        singleLine = editField.singleLine,
        modifier = Modifier.fillMaxWidth()
    )
}

inline fun <reified T> guessKeyboardOptions(editField: EditField<T>): KeyboardOptions {
	var type = editField.keyboardType

	if (type == null) {
		type = when(T::class) {
			Int::class, Long::class -> KeyboardType.Number
			Double::class, Float::class -> KeyboardType.Decimal
			else -> null
		}
	}

	return if (type != null) KeyboardOptions(keyboardType = type) else KeyboardOptions.Default
}