package com.jerboa.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.unit.TextUnit

// Set of Material typography styles to start with
fun generateTypography(baseFontSize: TextUnit): Typography {
    return Typography(
        bodyLarge = Typography().bodyLarge.copy(
            fontSize = baseFontSize,
            lineHeight = baseFontSize.times(1.3)
        ),
        bodyMedium = Typography().bodyMedium.copy(
            fontSize = baseFontSize.times(.8),
            lineHeight = baseFontSize.times(.8 * 1.3)
        ),
        titleMedium = Typography().titleMedium.copy(
            fontSize = baseFontSize,
            lineHeight = baseFontSize.times(1.3)
        ),
        titleLarge = Typography().titleLarge.copy(
            fontSize = baseFontSize.times(1.3),
            lineHeight = baseFontSize.times(1.3 * 1.3)
        )
    )
}
