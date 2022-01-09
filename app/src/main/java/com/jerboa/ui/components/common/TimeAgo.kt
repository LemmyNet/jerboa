package com.jerboa.ui.components.common

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.jerboa.datatypes.samplePersonSafe
import com.jerboa.prettyTime
import java.time.Instant
import java.util.*

@Composable
fun TimeAgo(dateStr: String) {
    val then = Date.from(Instant.parse(dateStr + "Z"))
    val ago = prettyTime.format(then)

    Text(
        text = ago,
    )
}

@Preview
@Composable
fun TimeAgoPreview() {
    TimeAgo(samplePersonSafe.published)
}
