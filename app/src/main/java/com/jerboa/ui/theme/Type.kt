package com.jerboa.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.unit.TextUnit

const val LINE_HEIGHT_MULTIPLIER = 1.3

// Set of Material typography styles to start with
fun generateTypography(baseFontSize: TextUnit): Typography {
    return Typography(
        bodyLarge = Typography().bodyLarge.copy(
            fontSize = baseFontSize,
            lineHeight = baseFontSize.times(LINE_HEIGHT_MULTIPLIER)
        ),
        bodyMedium = Typography().bodyMedium.copy(
            fontSize = baseFontSize.times(.8),
            lineHeight = baseFontSize.times(.8 * LINE_HEIGHT_MULTIPLIER)
        ),
        titleMedium = Typography().titleMedium.copy(
            fontSize = baseFontSize,
            lineHeight = baseFontSize.times(LINE_HEIGHT_MULTIPLIER)
        ),
        titleLarge = Typography().titleLarge.copy(
            fontSize = baseFontSize.times(LINE_HEIGHT_MULTIPLIER),
            lineHeight = baseFontSize.times(LINE_HEIGHT_MULTIPLIER * LINE_HEIGHT_MULTIPLIER)
        )
    )
}
