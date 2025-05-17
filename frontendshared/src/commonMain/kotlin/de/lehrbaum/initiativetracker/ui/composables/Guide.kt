package de.lehrbaum.initiativetracker.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.lehrbaum.initiativetracker.GlobalInstances
import de.lehrbaum.initiativetracker.ui.Constants

/**
 * A reusable guide component that shows a banner with information for the user.
 * The guide can be dismissed and will not be shown again.
 *
 * @param text The text to display in the guide
 * @param guideKey The key to use for storing the dismissed state in the settings repository
 * @param modifier The modifier to apply to the guide
 */
@Composable
fun Guide(
    text: String,
    guideKey: String,
    modifier: Modifier = Modifier
) {
    val settingsRepository = GlobalInstances.generalSettingsRepository

    val shouldShow by settingsRepository.showGuideFlow(guideKey).collectAsState(initial = false)

    if (shouldShow) {
        GuideContent(
            text = text,
            onDismiss = {
                settingsRepository.hideGuide(guideKey)
            },
            modifier = modifier
        )
    }
}

@Composable
private fun GuideContent(
    text: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(Constants.defaultPadding),
        elevation = 2.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
				.background(MaterialTheme.colors.surface.copy(alpha = 0.9f)) // Slightly muted surface
				.padding(Constants.defaultPadding)
                .fillMaxWidth()
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.body2, // Smaller font than body1
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.padding(start = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss guide",
                    tint = MaterialTheme.colors.primary
                )
            }
        }
    }
}
