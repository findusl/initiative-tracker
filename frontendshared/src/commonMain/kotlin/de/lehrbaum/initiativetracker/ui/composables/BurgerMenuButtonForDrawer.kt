package de.lehrbaum.initiativetracker.ui.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@Composable
fun BurgerMenuButtonForDrawer(drawerState: DrawerState) {
    val coroutineScope = rememberCoroutineScope()
    IconButton(
        onClick = { coroutineScope.launch { drawerState.open() } }
    ) {
        Icon(Icons.Default.Menu, contentDescription = "Burger menu to control drawer")
    }
}
