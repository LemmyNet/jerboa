package com.jerboa.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = text,
                )
            }
        },
        onClick = onClick,
        modifier = modifier,
    )
}

@Composable
fun MenuItem(
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    onClick: () -> Unit,
    highlight: Boolean,
) {
    MenuItem(
        text = text,
        icon = icon,
        onClick = onClick,
        modifier = if (highlight) {
            modifier.background(MaterialTheme.colorScheme.onBackground.copy(alpha = .1f))
        } else {
            modifier
        },
    )
}
