package de.lehrbaum.initiativetracker.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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