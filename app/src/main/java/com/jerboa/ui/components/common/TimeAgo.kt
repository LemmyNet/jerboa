package com.jerboa.ui.components.common

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.jerboa.datatypes.samplePersonSafe
import com.jerboa.prettyTime
import com.jerboa.sdf
import java.util.*


@Composable
fun TimeAgo(dateStr: String) {
  val then = sdf.parse(dateStr).time
  val ago = prettyTime.formatDuration(Date(then))

  Text(
    text = ago
  )
}

@Preview
@Composable
fun TimeAgoPreview() {
  TimeAgo(samplePersonSafe.published)
}
