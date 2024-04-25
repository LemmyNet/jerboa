package com.jerboa.ui.theme

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

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
        style = TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
        ),
    )
}
