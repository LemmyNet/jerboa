package com.jerboa.ui.components.common

import android.util.Log
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jerboa.R
import com.jerboa.datatypes.samplePerson
import com.jerboa.formatDuration
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
    val style = MaterialTheme.typography.labelMedium

    if (publishedPretty == null) {
        SmallErrorLabel(text = stringResource(R.string.time_ago_failed_to_parse))
        return
    }

    val afterPreceding =
        precedingString?.let {
            stringResource(R.string.time_ago_ago, it, publishedPretty)
        } ?: run { publishedPretty }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (updated !== null) {
            val updatedPretty = dateStringToPretty(updated, longTimeFormat)

            if (updatedPretty == null) {
                SmallErrorLabel(text = stringResource(R.string.time_ago_failed_to_parse))
            } else {
                val size = style.fontSize.value.dp
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = updatedPretty,
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(size),
                )
                Text(
                    text = updatedPretty,
                    style = style,
                    color = MaterialTheme.colorScheme.outline,
                    fontStyle = FontStyle.Italic,
                )
            }
        } else {
            Text(
                text = afterPreceding,
                color = MaterialTheme.colorScheme.outline,
                style = style,
            )
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
): String? =
    try {
        val publishedDate = Date.from(Instant.parse(dateStr))
        formatDuration(publishedDate, longTimeFormat)
    } catch (e: DateTimeParseException) {
        Log.d("TimeAgo", "Failed to parse date string: $dateStr", e)
        null
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
