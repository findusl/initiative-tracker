package de.lehrbaum.initiativetracker.ui.screen

import androidx.compose.runtime.Composable

@Composable
expect fun GeneralDialog(onDismissRequest: () -> Unit, content: @Composable () -> Unit)
