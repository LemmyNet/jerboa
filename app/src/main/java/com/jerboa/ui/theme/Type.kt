package com.jerboa.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with
fun generateTypography(baseFontSize: TextUnit): Typography {
    return Typography(
        body1 = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = baseFontSize,
            lineHeight = 1.3.em
        ),
        body2 = TextStyle(
            fontWeight = FontWeight.Normal,
            letterSpacing = 0.25.sp,
            fontSize = baseFontSize.times(.875),
            lineHeight = 1.3.em
        ),
        subtitle1 = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = baseFontSize,
            letterSpacing = 0.15.sp
        ),
        h6 = TextStyle(
            fontWeight = FontWeight.Medium,
            fontSize = baseFontSize.times(1.25),
            letterSpacing = 0.15.sp
        )
    )
}
