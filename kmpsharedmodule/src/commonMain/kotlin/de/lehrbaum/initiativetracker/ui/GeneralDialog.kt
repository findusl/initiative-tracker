package de.lehrbaum.initiativetracker.ui

import androidx.compose.runtime.Composable

@Composable
expect fun GeneralDialog(onDismissRequest: () -> Unit, content: @Composable () -> Unit)

@Composable
expect fun FullscreenDialog(onDismissRequest: () -> Unit, content: @Composable () -> Unit)