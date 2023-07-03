package com.jerboa.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.unit.TextUnit

const val FONT_LARGE_MULTIPLIER = 1.3
const val FONT_MEDIUM_MULTIPLIER = 0.8
const val FONT_SMALL_MULTIPLIER = 0.7

// Set of Material typography styles to start with
fun generateTypography(baseFontSize: TextUnit): Typography {
    return Typography(
        bodyLarge = Typography().bodyLarge.copy(
            fontSize = baseFontSize,
            lineHeight = baseFontSize.times(FONT_LARGE_MULTIPLIER),
        ),
        bodyMedium = Typography().bodyMedium.copy(
            fontSize = baseFontSize.times(FONT_MEDIUM_MULTIPLIER),
            lineHeight = baseFontSize.times(FONT_MEDIUM_MULTIPLIER * FONT_LARGE_MULTIPLIER),
        ),
        bodySmall = Typography().bodySmall.copy(
            fontSize = baseFontSize.times(FONT_SMALL_MULTIPLIER),
            lineHeight = baseFontSize.times(FONT_SMALL_MULTIPLIER * FONT_LARGE_MULTIPLIER),
        ),
        titleMedium = Typography().titleMedium.copy(
            fontSize = baseFontSize,
            lineHeight = baseFontSize.times(FONT_LARGE_MULTIPLIER),
        ),
        titleLarge = Typography().titleLarge.copy(
            fontSize = baseFontSize.times(FONT_LARGE_MULTIPLIER),
            lineHeight = baseFontSize.times(FONT_LARGE_MULTIPLIER),
        ),
        titleSmall = Typography().titleSmall.copy(
            fontSize = baseFontSize.times(FONT_MEDIUM_MULTIPLIER),
            lineHeight = baseFontSize.times(FONT_LARGE_MULTIPLIER),
        ),
    )
}
