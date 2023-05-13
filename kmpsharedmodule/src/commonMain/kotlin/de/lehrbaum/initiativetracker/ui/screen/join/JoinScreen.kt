package de.lehrbaum.initiativetracker.ui.screen.join

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import de.lehrbaum.initiativetracker.ui.screen.components.BurgerMenuButtonForDrawer

@Composable
fun JoinScreen(drawerState: DrawerState, onJoin: (Int) -> Unit, onCancel: () -> Unit, asHost: Boolean) {
	Scaffold(
		topBar = {
			TopAppBar(
				title = {
					val text = if(asHost) "Host existing combat" else "Join existing combat"
					Text(text, color = MaterialTheme.colors.onPrimary)
				},
				navigationIcon = { BurgerMenuButtonForDrawer(drawerState) }
			)
		}
	) {
		val sessionIdState = remember { mutableStateOf(0) }
		val textError = remember { mutableStateOf(false) }
		Column {
			Text("Please provide a SessionId")
			InputSessionIdTextField(sessionIdState, textError)
			Row(horizontalArrangement = Arrangement.SpaceEvenly) {
				Button(onClick = onCancel) {
					Text("Cancel")
				}
				Button(
					onClick = { onJoin(sessionIdState.value) },
					enabled = !textError.value
				) {
					Text("OK")
				}
			}
		}
	}
}

@Composable
fun InputSessionIdTextField(sessionIdState: MutableState<Int>, textError: MutableState<Boolean>) {
	var text by remember { mutableStateOf("0") }
	OutlinedTextField(
		value = text,
		onValueChange = {  input ->
			text = input
			val parsedInput = input.toIntOrNull()
			if (parsedInput != null) {
				sessionIdState.value = parsedInput
			}
			textError.value = parsedInput == null
		},
		label = { Text("SessionId") },
		isError = textError.value,
		modifier = Modifier.fillMaxWidth()
	)
}
