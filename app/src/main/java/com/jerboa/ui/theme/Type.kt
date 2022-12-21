package com.jerboa.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.unit.TextUnit

// Set of Material typography styles to start with
fun generateTypography(baseFontSize: TextUnit): Typography {
    return Typography(
        bodyLarge = Typography().bodyLarge.copy(fontSize = baseFontSize),
        bodyMedium = Typography().bodyMedium.copy(fontSize = baseFontSize.times(.8)),
        titleMedium = Typography().titleMedium.copy(fontSize = baseFontSize),
        titleLarge = Typography().titleLarge.copy(fontSize = baseFontSize)
    )
}
