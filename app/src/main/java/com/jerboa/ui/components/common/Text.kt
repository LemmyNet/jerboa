package com.jerboa.ui.components.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Preview(
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun TitlePreview() {
    Title("This is my title")
}

@Composable
fun Title(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
    )
}
