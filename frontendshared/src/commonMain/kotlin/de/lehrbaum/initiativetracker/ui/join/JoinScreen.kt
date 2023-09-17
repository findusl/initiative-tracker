package de.lehrbaum.initiativetracker.ui.join

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.lehrbaum.initiativetracker.ui.composables.BurgerMenuButtonForDrawer

@Composable
fun JoinScreen(drawerState: DrawerState, joinViewModel: JoinViewModel) {
	Scaffold(
		topBar = {
			TopAppBar(
				title = {
					val text = joinViewModel.title
					Text(text, color = MaterialTheme.colors.onPrimary)
				},
				navigationIcon = { BurgerMenuButtonForDrawer(drawerState) }
			)
		}
	) {
		Column {
			OutlinedTextField(
				value = joinViewModel.hostFieldContent,
				onValueChange = { input ->
					joinViewModel.hostFieldContent = input
				},
				label = { Text("Host") },
				isError = joinViewModel.hostFieldError,
				modifier = Modifier.fillMaxWidth()
			)
			OutlinedTextField(
				value = joinViewModel.combatIdFieldContent,
				onValueChange = { input ->
					joinViewModel.combatIdFieldContent = input
				},
				label = { Text("Combat Id (Optional)") },
				isError = joinViewModel.combatIdFieldError,
				modifier = Modifier.fillMaxWidth()
			)
			Row {
				Switch(
					checked = joinViewModel.secureConnectionChosen,
					onCheckedChange = { joinViewModel.secureConnectionChosen = it }
				)
				Text("Use Secure Connection?")
			}
			Row(horizontalArrangement = Arrangement.SpaceEvenly) {
				Button(
					onClick = { joinViewModel.onConnectPressed() },
					enabled = joinViewModel.inputsAreValid
				) {
					Text("Connect", modifier = Modifier.padding(start = 16.dp).align(CenterVertically))
				}
			}
		}
	}
}
