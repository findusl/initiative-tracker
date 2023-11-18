package de.lehrbaum.initiativetracker.ui.shared

import androidx.compose.material.SnackbarDuration
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable

@Stable
sealed interface SnackbarState {
    val text: String
    val duration: SnackbarDuration

    data class Text(
        override val text: String,
        override val duration: SnackbarDuration = SnackbarDuration.Short
    ): SnackbarState

    data class Copyable(
        override val text: String,
        override val duration: SnackbarDuration = SnackbarDuration.Long,
        val copyText: String
    ): SnackbarState

}

fun MutableState<SnackbarState?>.showText(text: String, duration: SnackbarDuration = SnackbarDuration.Long) {
	value = SnackbarState.Text(text, duration)
}
