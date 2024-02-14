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
import com.jerboa.SHOW_UPVOTE_PCT_THRESHOLD
import com.jerboa.datatypes.VoteDisplayMode
import com.jerboa.datatypes.samplePerson
import com.jerboa.datatypes.samplePost
import com.jerboa.feat.formatPercent
import com.jerboa.feat.upvotePercent
import com.jerboa.formatDuration
import com.jerboa.ui.theme.SMALL_PADDING
import com.jerboa.ui.theme.muted
import java.time.Instant
import java.time.format.DateTimeParseException
import java.util.Date

@Composable
fun TimeAgo(
    published: String,
    modifier: Modifier = Modifier,
    updated: String? = null,
    precedingString: String? = null,
    longTimeFormat: Boolean = false,
) {
    val publishedPretty = dateStringToPretty(published, longTimeFormat)

    if (publishedPretty == null) {
        SmallErrorLabel(text = stringResource(R.string.time_ago_failed_to_parse))
        return
    }

    val afterPreceding =
        precedingString?.let {
            stringResource(R.string.time_ago_ago, it, publishedPretty)
        } ?: run { publishedPretty }

    Row(modifier = modifier) {
        Text(
            text = afterPreceding,
            color = MaterialTheme.colorScheme.onBackground.muted,
            style = MaterialTheme.typography.labelMedium,
        )

        updated?.also {
            DotSpacer(
                padding = SMALL_PADDING,
                style = MaterialTheme.typography.labelMedium,
            )
            val updatedPretty = dateStringToPretty(it, longTimeFormat)

            if (updatedPretty == null) {
                SmallErrorLabel(text = stringResource(R.string.time_ago_failed_to_parse))
            } else {
                Text(
                    text = "($updatedPretty)",
                    style = MaterialTheme.typography.labelMedium,
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
fun dateStringToPretty(
    dateStr: String,
    longTimeFormat: Boolean = false,
): String? {
    return try {
        // TODO: v0.18.4_deprecated Remove this hack once backward API compatibility is implemented
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
    TimeAgo(
        published = samplePerson.published,
        updated = samplePerson.updated,
    )
}

@Composable
fun ScoreAndTime(
    score: Long,
    upvotes: Long,
    downvotes: Long,
    myVote: Int,
    published: String,
    updated: String?,
    isExpanded: Boolean = true,
    collapsedCommentsCount: Long = 0,
    isNsfw: Boolean = false,
    voteDisplayMode: VoteDisplayMode,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(SMALL_PADDING),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NsfwBadge(isNsfw)
        CollapsedIndicator(visible = !isExpanded, descendants = collapsedCommentsCount)
        Spacer(modifier = Modifier.padding(end = SMALL_PADDING))
        val upvotePct = upvotePercent(
            upvotes = upvotes,
            downvotes = downvotes,
        )
        when (voteDisplayMode) {
            VoteDisplayMode.Full -> {
                LargeVoteIndicator(data = score.toString(), myVote = myVote)
                DotSpacer(style = MaterialTheme.typography.labelMedium)
                if (upvotePct < SHOW_UPVOTE_PCT_THRESHOLD) {
                    SmallVoteIndicator(data = "$downvotes↓")
                    DotSpacer(style = MaterialTheme.typography.labelMedium)
                }
            }
            VoteDisplayMode.ScoreAndUpvotePercentage -> {
                LargeVoteIndicator(data = score.toString(), myVote = myVote)
                DotSpacer(style = MaterialTheme.typography.labelMedium)
                if (upvotePct < SHOW_UPVOTE_PCT_THRESHOLD) {
                    SmallVoteIndicator(data = formatPercent(upvotePct))
                    DotSpacer(style = MaterialTheme.typography.labelMedium)
                }
            }
            VoteDisplayMode.UpvotePercentage -> {
                LargeVoteIndicator(data = formatPercent(upvotePct), myVote = myVote)
                DotSpacer(style = MaterialTheme.typography.labelMedium)
            }
            VoteDisplayMode.Score -> {
                LargeVoteIndicator(data = score.toString(), myVote = myVote)
                DotSpacer(style = MaterialTheme.typography.labelMedium)
            }
            VoteDisplayMode.HideAll -> {}
        }
        TimeAgo(published = published, updated = updated)
    }
}

@Composable
private fun LargeVoteIndicator(
    data: String,
    myVote: Int,
) {
    Text(
        text = data,
        color = scoreColor(myVote = myVote),
        style = MaterialTheme.typography.labelMedium,
        fontSize = MaterialTheme.typography.labelMedium.fontSize.times(1.3),
    )
}

@Composable
private fun SmallVoteIndicator(data: String) {
    Text(
        text = data,
        color = MaterialTheme.colorScheme.onBackground.muted,
        style = MaterialTheme.typography.labelMedium,
    )
}

@Preview
@Composable
fun ScoreFullAndTimePreview() {
    ScoreAndTime(
        score = 25,
        myVote = -1,
        upvotes = 10,
        downvotes = 15,
        published = samplePost.published,
        updated = samplePost.updated,
        voteDisplayMode = VoteDisplayMode.Full,
    )
}

@Preview
@Composable
fun ScoreAndUpvotePctAndTimePreview() {
    ScoreAndTime(
        score = 25,
        myVote = -1,
        upvotes = 10,
        downvotes = 15,
        published = samplePost.published,
        updated = samplePost.updated,
        voteDisplayMode = VoteDisplayMode.ScoreAndUpvotePercentage,
    )
}

@Preview
@Composable
fun UpvotePctAndTimePreview() {
    ScoreAndTime(
        score = 25,
        myVote = -1,
        upvotes = 10,
        downvotes = 15,
        published = samplePost.published,
        updated = samplePost.updated,
        voteDisplayMode = VoteDisplayMode.UpvotePercentage,
    )
}

@Preview
@Composable
fun ScoreAndTimePreview() {
    ScoreAndTime(
        score = 25,
        myVote = -1,
        upvotes = 10,
        downvotes = 15,
        published = samplePost.published,
        updated = samplePost.updated,
        voteDisplayMode = VoteDisplayMode.Score,
    )
}

@Preview
@Composable
fun HideAllAndTimePreview() {
    ScoreAndTime(
        score = 25,
        myVote = -1,
        upvotes = 10,
        downvotes = 15,
        published = samplePost.published,
        updated = samplePost.updated,
        voteDisplayMode = VoteDisplayMode.HideAll,
    )
}

@Composable
fun CollapsedIndicator(
    visible: Boolean,
    descendants: Long,
) {
    AnimatedVisibility(
        visible = visible && descendants > 0,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Column(modifier = Modifier.wrapContentSize(Alignment.Center)) {
            Box(
                modifier =
                    Modifier
                        .clip(RoundedCornerShape(2.dp))
                        .background(MaterialTheme.colorScheme.secondary)
                        .padding(horizontal = SMALL_PADDING),
            ) {
                Text(
                    text = "+$descendants",
                    style = MaterialTheme.typography.labelMedium,
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
                modifier =
                    Modifier
                        .clip(RoundedCornerShape(2.dp))
                        .background(MaterialTheme.colorScheme.secondary)
                        .padding(horizontal = SMALL_PADDING),
            ) {
                Text(
                    text = "NSFW",
                    style = MaterialTheme.typography.labelMedium,
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

@Composable
fun SmallErrorLabel(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.labelSmall,
    )
}

@Preview
@Composable
fun SmallErrorLabelPreview() {
    SmallErrorLabel(stringResource(id = R.string.time_ago_failed_to_parse))
}
