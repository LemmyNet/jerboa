package com.jerboa.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun TextBadge(
    text: String,
    verticalTextPadding: Float = 0f,
    horizontalTextPadding: Float = 6f,
    textColor: Color = MaterialTheme.colorScheme.onTertiary,
    textStyle: TextStyle,
    containerColor: Color = MaterialTheme.colorScheme.tertiary,
    containerRadius: Float = 4f,
) {
    Box(
        modifier =
        Modifier
            .clip(RoundedCornerShape(containerRadius.dp))
            .background(containerColor),
    ) {
        Text(
            text = text,
            style = textStyle,
            overflow = TextOverflow.Clip,
            maxLines = 1,
            color = textColor,
            modifier =
            Modifier
                .padding(horizontalTextPadding.dp, verticalTextPadding.dp),
        )
    }
}
