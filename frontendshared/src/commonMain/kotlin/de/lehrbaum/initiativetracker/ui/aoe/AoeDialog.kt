package de.lehrbaum.initiativetracker.ui.aoe

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Checkbox
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.lehrbaum.initiativetracker.ui.GeneralDialog
import de.lehrbaum.initiativetracker.ui.composables.DiceRollingTextField
import de.lehrbaum.initiativetracker.ui.composables.OkCancelButtonRow
import de.lehrbaum.initiativetracker.ui.composables.rememberCoroutineScope
import de.lehrbaum.initiativetracker.ui.keyevents.defaultFocussed
import kotlinx.coroutines.launch

@Composable
fun AoeDamageDialog(aoeDamageViewModel: AoeDamageViewModel) {
	GeneralDialog(aoeDamageViewModel::onDismiss) {
		Content(aoeDamageViewModel)
	}
}

@Composable
private fun Content(aoeDamageViewModel: AoeDamageViewModel) {
	val coroutineScope = rememberCoroutineScope(aoeDamageViewModel)
	Column {
		TabRow(selectedTabIndex = aoeDamageViewModel.activeTab) {
			Tab(text = { Text("Targets") }, selected = aoeDamageViewModel.activeTab == 0, onClick = { aoeDamageViewModel.activeTab = 0 })
			Tab(text = { Text("General") }, selected = aoeDamageViewModel.activeTab == 1, onClick = { aoeDamageViewModel.activeTab = 1 })
		}

		TabContent(aoeDamageViewModel)

		OkCancelButtonRow(
			submittable = true,
			onCancel = aoeDamageViewModel::onDismiss,
			onSubmit = { coroutineScope.launch { aoeDamageViewModel.onSubmitPressed() } },
			showSubmitLoadingSpinner = aoeDamageViewModel.isSubmitting,
		)
	}
}

@Composable
private fun TabContent(aoeDamageViewModel: AoeDamageViewModel) {
	when (aoeDamageViewModel.activeTab) {
		0 -> TargetsTab(aoeDamageViewModel)
		1 -> GeneralTab(aoeDamageViewModel)
	}
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun TargetsTab(aoeDamageViewModel: AoeDamageViewModel) {
	LazyColumn {
		items(aoeDamageViewModel.targets, key = TargetViewModel::id) { target ->
			ListItem(
				modifier = Modifier.clickable {
					aoeDamageViewModel.onTargetPressed(target)
				},
			) {
				Row {
					Checkbox(target.isSelected, onCheckedChange = null)
					Text(target.name)
				}
			}
		}
	}
}

@Composable
private fun GeneralTab(viewModel: AoeDamageViewModel) {
	// This text should be centered
	DiceRollingTextField(
		onNumberChanged = {
			viewModel.onDamageChanged(it)
		},
		modifier = Modifier
			.defaultFocussed(viewModel),
		onInputValidChanged = { viewModel.isDamageValid = it },
		label = "Damage",
		placeholder = "5d8+1",
	)
}
