package com.jerboa.ui.components.common

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import com.jerboa.datatypes.samplePersonSafe
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
