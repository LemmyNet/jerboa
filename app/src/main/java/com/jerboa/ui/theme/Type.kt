package com.jerboa.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.em

val LINE_HEIGHT = 1.3.em

// Set of Material typography styles to start with
fun generateTypography(baseFontSize: TextUnit): Typography {
    return Typography(
        bodyLarge = Typography().bodyLarge.copy(
            fontSize = baseFontSize,
            lineHeight = LINE_HEIGHT
        ),
        bodyMedium = Typography().bodyMedium.copy(
            fontSize = baseFontSize.times(.8),
            lineHeight = LINE_HEIGHT
        ),
        titleMedium = Typography().titleMedium.copy(
            fontSize = baseFontSize,
            lineHeight = LINE_HEIGHT
        ),
        titleLarge = Typography().titleLarge.copy(
            fontSize = baseFontSize.times(1.3),
            lineHeight = LINE_HEIGHT
        )
    )
}
