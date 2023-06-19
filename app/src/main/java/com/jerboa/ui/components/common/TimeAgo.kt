package com.jerboa.ui.components.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jerboa.R
import com.jerboa.datatypes.samplePerson
import com.jerboa.datatypes.samplePost
import com.jerboa.formatDuration
import com.jerboa.ui.theme.SMALL_PADDING
import com.jerboa.ui.theme.muted
import java.time.Instant
import java.util.Date

@Composable
fun TimeAgo(
    published: String,
    updated: String? = null,
    precedingString: String? = null,
    longTimeFormat: Boolean = false,
) {
    val publishedPretty = dateStringToPretty(published, longTimeFormat)

    val afterPreceding = precedingString?.let {
        stringResource(R.string.time_ago_ago, it, publishedPretty)
    } ?: run { publishedPretty }

    Row {
        Text(
            text = afterPreceding,
            color = MaterialTheme.colorScheme.onBackground.muted,
            style = MaterialTheme.typography.bodyMedium,
        )

        updated?.also {
            val updatedPretty = dateStringToPretty(it, longTimeFormat)

            DotSpacer(
                padding = SMALL_PADDING,
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = "($updatedPretty)",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.muted,
                fontStyle = FontStyle.Italic,
            )
        }
    }
}

fun dateStringToPretty(dateStr: String, longTimeFormat: Boolean = false): String {
    val publishedDate = Date.from(Instant.parse(dateStr + "Z"))
    return formatDuration(publishedDate, longTimeFormat)
}

@Preview
@Composable
fun TimeAgoPreview() {
    TimeAgo(samplePerson.published, samplePerson.updated)
}

@Composable
fun ScoreAndTime(
    score: Int,
    myVote: Int?,
    published: String,
    updated: String?,
    isExpanded: Boolean = true,
    collapsedCommentsCount: Int = 0,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(SMALL_PADDING),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CollapsedIndicator(visible = !isExpanded, descendants = collapsedCommentsCount)
        Spacer(modifier = Modifier.padding(end = SMALL_PADDING))
        Text(
            text = score.toString(),
            color = scoreColor(myVote = myVote),
            fontSize = MaterialTheme.typography.bodyMedium.fontSize.times(1.3),
        )
        DotSpacer(0.dp, MaterialTheme.typography.bodyMedium)
        TimeAgo(published = published, updated = updated)
    }
}

@Preview
@Composable
fun ScoreAndTimePreview() {
    ScoreAndTime(
        score = 23,
        myVote = -1,
        published = samplePost.published,
        updated = samplePost.updated,
    )
}

@Composable
fun CollapsedIndicator(visible: Boolean, descendants: Int) {
    AnimatedVisibility(
        visible = visible && descendants > 0,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Column(modifier = Modifier.wrapContentSize(Alignment.Center)) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.secondary)
                    .padding(horizontal = SMALL_PADDING),
            ) {
                Text(
                    text = "+$descendants",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondary,
                )
            }
        }
    }
}

@Preview
@Composable
fun CollapsedIndicatorPreview() {
    CollapsedIndicator(visible = true, descendants = 23)
}
