package de.lehrbaum.initiativetracker.ui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun OkCancelButtonRow(
	submittable: State<Boolean>,
	onCancel: () -> Unit,
	onSubmit: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(
            onClick = { onCancel() },
            shape = RoundedCornerShape(50.dp),
            modifier = Modifier
                .height(50.dp)
                .fillMaxWidth()
                .weight(1f)
        ) {
            Text(text = "Cancel")
        }
        Button(
            onClick = onSubmit,
            shape = RoundedCornerShape(50.dp),
			enabled = submittable.value,
            modifier = Modifier
                .height(50.dp)
                .fillMaxWidth()
                .weight(1f)
        ) {
            Text(text = "Ok")
        }
    }
}

@Composable
fun OkCancelButtonRow(
	submittable: State<Boolean>,
	onCancel: () -> Unit,
	onSubmit: suspend () -> Unit,
	coroutineScope: CoroutineScope,
) {
	var showLoadingSpinner by remember { mutableStateOf(false) }
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(
            onClick = { onCancel() },
            shape = RoundedCornerShape(50.dp),
            modifier = Modifier
                .height(50.dp)
                .fillMaxWidth()
                .weight(1f)
        ) {
            Text(text = "Cancel")
        }
        Button(
            onClick = {
				if (showLoadingSpinner) return@Button
				coroutineScope.launch {
					showLoadingSpinner = true
					onSubmit()
					showLoadingSpinner = false
				}
			},
            shape = RoundedCornerShape(50.dp),
			enabled = submittable.value,
            modifier = Modifier
                .height(50.dp)
                .fillMaxWidth()
                .weight(1f)
        ) {
			if (showLoadingSpinner) {
				CircularProgressIndicator(
					color = MaterialTheme.colorScheme.onPrimary,
					strokeWidth = 3.dp,
					modifier = Modifier.padding(start = 8.dp).size(28.dp)
				)
			}
            Text(text = "Ok")
        }
    }
}
