package com.jerboa.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle

@Composable
fun MenuItem(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textModifier: Modifier = Modifier,
    icon: ImageVector? = null,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
) {
    DropdownMenuItem(
        text = {
            Text(
                text = text,
                style = textStyle,
                modifier = textModifier,
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
