package com.jerboa.ui.components.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jerboa.datatypes.samplePersonSafe
import com.jerboa.datatypes.samplePost
import com.jerboa.prettyTime
import com.jerboa.prettyTimeShortener
import com.jerboa.ui.theme.SMALL_PADDING
import com.jerboa.ui.theme.muted
import java.time.Instant
import java.util.Date

@Composable
fun TimeAgo(
    published: String,
    updated: String? = null,
    precedingString: String? = null,
    includeAgo: Boolean = false
) {
    val publishedPretty = dateStringToPretty(published, includeAgo)

    val afterPreceding = precedingString?.let {
        "$it $publishedPretty ago"
    } ?: run { publishedPretty }

    Row {
        Text(
            text = afterPreceding,
            color = MaterialTheme.colorScheme.onBackground.muted,
            style = MaterialTheme.typography.bodyMedium
        )

        updated?.also {
            val updatedPretty = dateStringToPretty(it, includeAgo)

            DotSpacer(
                padding = SMALL_PADDING,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "($updatedPretty)",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.muted,
                fontStyle = FontStyle.Italic
            )
        }
    }
}

fun dateStringToPretty(dateStr: String, includeAgo: Boolean = false): String {
    val publishedDate = Date.from(Instant.parse(dateStr + "Z"))
    val prettyPublished = prettyTime.formatDuration(publishedDate)

    return if (includeAgo) {
        prettyPublished
    } else {
        prettyTimeShortener(prettyPublished)
    }
}

@Preview
@Composable
fun TimeAgoPreview() {
    TimeAgo(samplePersonSafe.published, samplePersonSafe.updated)
}

@Composable
fun ScoreAndTime(
    score: Int,
    myVote: Int?,
    published: String,
    updated: String?
) {
    val expandSize = if (myVote == 0) { 1.0 } else { 1.3 }

    Row(
        horizontalArrangement = Arrangement.spacedBy(SMALL_PADDING),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = score.toString(),
            color = scoreColor(myVote = myVote),
            fontSize = MaterialTheme.typography.bodyMedium.fontSize.times(expandSize)
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
        updated = samplePost.updated
    )
}
