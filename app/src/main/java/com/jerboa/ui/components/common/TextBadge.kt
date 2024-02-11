package com.jerboa.ui.components.common

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
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
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jerboa.INSTANCE_FONT_SIZE
import com.jerboa.hostName
import com.jerboa.ui.theme.muted

@OptIn(ExperimentalFoundationApi::class)
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
            maxLines = 1,
            color = textColor,
            modifier =
                Modifier
                    .padding(horizontalTextPadding.dp, verticalTextPadding.dp)
                    .basicMarquee(),
        )
    }
}

/**
 * Displays activitypub items (communities, users), with a smaller @instance shown
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ItemAndInstanceTitle(
    modifier: Modifier = Modifier,
    title: String,
    actorId: String?,
    local: Boolean,
    itemColor: Color = MaterialTheme.colorScheme.primary,
    itemStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    instanceColor: Color = MaterialTheme.colorScheme.onSurface.muted,
    instanceStyle: TextStyle = MaterialTheme.typography.bodySmall.copy(
        fontSize = INSTANCE_FONT_SIZE.sp,
    ),
) {
    val serverStr = if (!local) {
        actorId?.let { aId ->
            "@${hostName(aId)}"
        } ?: run {
            null
        }
    } else {
        null
    }

    Text(
        text = buildAnnotatedString {
            withStyle(
                style = itemStyle.toSpanStyle().copy(color = itemColor),
            ) {
                append(title)
            }
            serverStr?.let { server ->
                withStyle(
                    style = instanceStyle.toSpanStyle().copy(
                        color = instanceColor,
                    ),
                ) {
                    append(server)
                }
            }
        },
        maxLines = 1,
        modifier = modifier.basicMarquee(),
    )
}
