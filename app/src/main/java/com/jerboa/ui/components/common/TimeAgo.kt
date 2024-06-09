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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jerboa.R
import com.jerboa.SHOW_UPVOTE_PCT_THRESHOLD
import com.jerboa.api.API
import com.jerboa.datatypes.samplePerson
import com.jerboa.datatypes.samplePost
import com.jerboa.feat.InstantScores
import com.jerboa.feat.formatPercent
import com.jerboa.feat.upvotePercent
import com.jerboa.formatDuration
import com.jerboa.ui.theme.SMALL_PADDING
import com.jerboa.ui.theme.muted
import io.github.z4kn4fein.semver.Version
import it.vercruysse.lemmyapi.v0x19.datatypes.LocalUserVoteDisplayMode
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
        val publishedDate = Date.from(Instant.parse(dateStr))
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
    instantScores: InstantScores,
    published: String,
    updated: String?,
    isExpanded: Boolean = true,
    collapsedCommentsCount: Long = 0,
    isNsfw: Boolean = false,
    voteDisplayMode: LocalUserVoteDisplayMode,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(SMALL_PADDING),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NsfwBadge(isNsfw)
        CollapsedIndicator(visible = !isExpanded, descendants = collapsedCommentsCount)
        Spacer(modifier = Modifier.padding(end = SMALL_PADDING))
        val upvotePct = upvotePercent(
            upvotes = instantScores.upvotes,
            downvotes = instantScores.downvotes,
        )

        // If the show_scores is disabled, and we are the instance is pre 0.19.4, we fallback to legacy behaviour
        val version = API.getInstanceOrNull()?.version
        val legacyScoresHidden = version != null && version < Version(0, 19, 4) && !voteDisplayMode.score

        // A special case for scores, where if both are enabled,
        // and the score is the same as the upvotes, then hide the score
        val hideScore =
            voteDisplayMode.score && voteDisplayMode.upvotes && instantScores.score == instantScores.upvotes

        if (!legacyScoresHidden) {
            if (voteDisplayMode.score && !hideScore) {
                VoteIndicator(
                    data = instantScores.score.toString(),
                    myVote = instantScores.myVote,
                    iconAndDescription = Pair(
                        Icons.Outlined.FavoriteBorder,
                        stringResource(id = R.string.score),
                    ),
                )
            }
            if (voteDisplayMode.upvote_percentage && (upvotePct < SHOW_UPVOTE_PCT_THRESHOLD)) {
                // Always mute the color
                VoteIndicator(data = formatPercent(upvotePct), myVote = 0)
            }
            if (voteDisplayMode.upvotes) {
                // Mute color if not 1
                val myVote = if (instantScores.myVote == 1) 1 else 0
                VoteIndicator(
                    data = instantScores.upvotes.toString(),
                    myVote = myVote,
                    iconAndDescription = Pair(
                        Icons.Outlined.ArrowUpward,
                        stringResource(id = R.string.upvoted),
                    ),
                )
            }
            if (voteDisplayMode.downvotes && instantScores.downvotes > 0) {
                // Mute color if not -1
                val myVote = if (instantScores.myVote == -1) -1 else 0
                VoteIndicator(
                    data = instantScores.downvotes.toString(),
                    myVote = myVote,
                    iconAndDescription = Pair(
                        Icons.Outlined.ArrowDownward,
                        stringResource(id = R.string.downvoted),
                    ),
                )
            }
            // Only show this spacer if at least one of the fields is enabled
            if (voteDisplayMode.score || voteDisplayMode.upvotes || voteDisplayMode.downvotes || voteDisplayMode.upvote_percentage) {
                DotSpacer(style = MaterialTheme.typography.labelMedium)
            }
        }
        TimeAgo(published = published, updated = updated)
    }
}

@Composable
private fun VoteIndicator(
    data: String,
    myVote: Int,
    iconAndDescription: Pair<ImageVector, String>? = null,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = data,
            color = scoreColor(myVote = myVote),
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 0.dp),
        )
        iconAndDescription?.let {
            val size = MaterialTheme.typography.labelMedium.fontSize.value.dp
            Icon(
                imageVector = iconAndDescription.first,
                contentDescription = iconAndDescription.second,
                tint = scoreColor(myVote = myVote),
                modifier = Modifier.size(size),
            )
        }
    }
}

@Preview
@Composable
fun UpvoteAndDownvotePreview() {
    ScoreAndTime(
        instantScores = InstantScores(
            score = 25,
            myVote = -1,
            upvotes = 10,
            downvotes = 15,
        ),
        published = samplePost.published,
        updated = samplePost.updated,
        voteDisplayMode = LocalUserVoteDisplayMode(
            local_user_id = -1,
            score = false,
            upvotes = true,
            downvotes = true,
            upvote_percentage = false,
        ),
    )
}

@Preview
@Composable
fun ScoreAndUpvotePctAndTimePreview() {
    ScoreAndTime(
        instantScores = InstantScores(
            score = 25,
            myVote = 1,
            upvotes = 10,
            downvotes = 15,
        ),
        published = samplePost.published,
        updated = samplePost.updated,
        voteDisplayMode = LocalUserVoteDisplayMode(
            local_user_id = -1,
            score = true,
            upvote_percentage = true,
            upvotes = false,
            downvotes = false,
        ),
    )
}

@Preview
@Composable
fun UpvotePctAndTimePreview() {
    ScoreAndTime(
        instantScores = InstantScores(
            score = 25,
            myVote = -1,
            upvotes = 10,
            downvotes = 15,
        ),
        published = samplePost.published,
        updated = samplePost.updated,
        voteDisplayMode = LocalUserVoteDisplayMode(
            local_user_id = -1,
            upvote_percentage = true,
            score = false,
            upvotes = false,
            downvotes = false,
        ),
    )
}

@Preview
@Composable
fun ScoreAndTimePreview() {
    ScoreAndTime(
        instantScores = InstantScores(
            score = 25,
            myVote = -1,
            upvotes = 10,
            downvotes = 15,
        ),
        published = samplePost.published,
        updated = samplePost.updated,
        voteDisplayMode = LocalUserVoteDisplayMode(
            local_user_id = -1,
            score = true,
            upvote_percentage = false,
            upvotes = false,
            downvotes = false,
        ),
    )
}

@Preview
@Composable
fun HideAllAndTimePreview() {
    ScoreAndTime(
        instantScores = InstantScores(
            score = 25,
            myVote = -1,
            upvotes = 10,
            downvotes = 15,
        ),
        published = samplePost.published,
        updated = samplePost.updated,
        voteDisplayMode = LocalUserVoteDisplayMode(
            local_user_id = -1,
            score = false,
            upvote_percentage = false,
            upvotes = false,
            downvotes = false,
        ),
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
