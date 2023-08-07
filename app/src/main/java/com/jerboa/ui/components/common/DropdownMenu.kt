package com.jerboa.ui.components.common

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun MenuItem(
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    onClick: () -> Unit,
) {
    DropdownMenuItem(
        text = {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        leadingIcon = {
            icon?.also { ico ->
                InboxIconAndBadge(
                    icon = ico,
                    contentDescription = text,
                    iconBadgeCount = null,
                )
            }
        },
        onClick = onClick,
        modifier = modifier,
    )
}
