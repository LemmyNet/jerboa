package com.jerboa.ui.components.common

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.jerboa.datatypes.samplePersonSafe
import com.jerboa.prettyTime
import com.jerboa.prettyTimeShortener
import com.jerboa.ui.theme.Muted
import java.time.Instant
import java.util.*

@Composable
fun TimeAgo(dateStr: String, precedingString: String? = null, includeAgo: Boolean = false) {
    val then = Date.from(Instant.parse(dateStr + "Z"))
    val pt = prettyTime.formatDuration(then)

    val pretty = if (includeAgo) {
        pt
    } else {
        prettyTimeShortener(pt)
    }

    val afterPreceding = precedingString?.let {
        "$it $pretty ago"
    } ?: run { pretty }

    Text(
        text = afterPreceding,
        color = Muted,
    )
}

@Preview
@Composable
fun TimeAgoPreview() {
    TimeAgo(samplePersonSafe.published)
}
