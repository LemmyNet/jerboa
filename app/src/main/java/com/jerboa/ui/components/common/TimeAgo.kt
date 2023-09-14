package com.jerboa.ui.components.common

import android.util.Log
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
import java.time.format.DateTimeParseException
import java.util.Date
import kotlin.jvm.Throws

@Composable
fun TimeAgo(
    published: String,
    updated: String? = null,
    precedingString: String? = null,
    longTimeFormat: Boolean = false,
) {
    val publishedPretty = dateStringToPretty(published, longTimeFormat)

    if (publishedPretty == null) {
        Text(
            text = stringResource(R.string.time_ago_failed_to_parse),
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.labelSmall,
        )
        return
    }

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
            DotSpacer(
                padding = SMALL_PADDING,
                style = MaterialTheme.typography.bodyMedium,
            )
            val updatedPretty = dateStringToPretty(it, longTimeFormat)

            if (updatedPretty == null) {
                Text(
                    text = stringResource(R.string.time_ago_failed_to_parse),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall,
                )
            } else {
                Text(
                    text = "($updatedPretty)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.muted,
                    fontStyle = FontStyle.Italic,
                )
            }
        }
    }
}

/**
 * Converts a date string to a pretty string like "2 hours" or "2h"
 *
 * @param dateStr The date string to convert
 * @param longTimeFormat If true, use a long time format like "2 hours, 3 minutes ago"
 * @return The pretty string, or null if the date string could not be parsed
 */
fun dateStringToPretty(dateStr: String, longTimeFormat: Boolean = false): String? {
    return try {
        // TODO: Remove this hack once backward API compatibility is implemented
        // pre 0.19 Datetimes didn't have a timezone, so we add one here
        val withTimezone = if (dateStr.last() == 'Z') dateStr else dateStr + "Z"
        val publishedDate = Date.from(Instant.parse(withTimezone))
        formatDuration(publishedDate, longTimeFormat)
    } catch (e: DateTimeParseException) {
        Log.d("TimeAgo", "Failed to parse date string: $dateStr", e)
        null
    }
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
    isNsfw: Boolean = false,
    showScores: Boolean,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(SMALL_PADDING),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NsfwBadge(isNsfw)
        CollapsedIndicator(visible = !isExpanded, descendants = collapsedCommentsCount)
        Spacer(modifier = Modifier.padding(end = SMALL_PADDING))
        if (showScores) {
            Text(
                text = score.toString(),
                color = scoreColor(myVote = myVote),
                fontSize = MaterialTheme.typography.bodyMedium.fontSize.times(1.3),
            )
        }
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
        showScores = true,
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

@Composable
fun NsfwBadge(visible: Boolean) {
    AnimatedVisibility(
        visible = visible,
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
                    text = "NSFW",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondary,
                )
            }
        }
    }
}

@Preview
@Composable
fun NsfwBadgePreview() {
    NsfwBadge(visible = true)
}
