package de.lehrbaum.initiativetracker.ui.screen.components

import androidx.compose.material.SnackbarHostState
import androidx.compose.material.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import de.lehrbaum.initiativetracker.ui.model.shared.SnackbarState
import kotlinx.coroutines.launch

@Composable
fun SnackbarHostState.bindSnackbarState(stateHolder: MutableState<SnackbarState?>) {
    val coroutineScope = rememberCoroutineScope()
    val state = stateHolder.value
    if (state != null) {
		stateHolder.value = null // ensures this is not called again with the same value
        coroutineScope.launch {
            val label = when (state) {
                is SnackbarState.Copyable -> "Copy"
                else -> null
            }
            val result = showSnackbar(state.text, label, state.duration)
            if (result == SnackbarResult.ActionPerformed) {
                when (state) {
                    is SnackbarState.Copyable -> {
                        TODO("Implement copy in multiplatform")
                        /*val clipboard = getSystemService(requireContext(), ClipboardManager::class.java)
                       val clip = ClipData.newPlainText("Combat Session Id", sessionCode.toString())
                       clipboard!!.setPrimaryClip(clip)
                         */
                    }
                    else -> {}
                }
            }
        }
    }
}


