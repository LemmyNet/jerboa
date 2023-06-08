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
    backgroundColor: Color,
    textColor: Color,
    containerRadius: Float,
    style: TextStyle
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(containerRadius.dp))
            .background(backgroundColor),
    ) {
        Text(
            text = text,
            style = style,
            overflow = TextOverflow.Clip,
            maxLines = 1,
            color = textColor,
            modifier = Modifier
                .padding(6.dp, 0.dp),
        )
    }
}

