package com.jerboa.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.unit.sp

const val FONT_SMALL_ADD = -7
const val FONT_MEDIUM_ADD = -3
const val FONT_LARGE_ADD = 0
const val LINE_HEIGHT_MULTIPLIER = 1.5

// Set of Material typography styles to start with
// Example from here: https://egeniq.com/blog/dynamic-font-sizes-with-jetpack-compose/
fun generateTypography(baseFontSize: Int): Typography {
    return Typography(
        bodySmall =
            Typography().bodySmall.copy(
                fontSize = baseFontSize.plus(FONT_SMALL_ADD).sp,
                lineHeight = baseFontSize.plus(FONT_SMALL_ADD).times(LINE_HEIGHT_MULTIPLIER).sp,
            ),
        bodyMedium =
            Typography().bodyMedium.copy(
                fontSize = baseFontSize.plus(FONT_MEDIUM_ADD).sp,
                lineHeight = baseFontSize.plus(FONT_MEDIUM_ADD).times(LINE_HEIGHT_MULTIPLIER).sp,
            ),
        bodyLarge =
            Typography().bodyLarge.copy(
                fontSize = baseFontSize.plus(FONT_LARGE_ADD).sp,
                lineHeight = baseFontSize.plus(FONT_LARGE_ADD).times(LINE_HEIGHT_MULTIPLIER).sp,
            ),
        titleSmall =
            Typography().titleSmall.copy(
                fontSize = baseFontSize.plus(FONT_SMALL_ADD).sp,
                lineHeight = baseFontSize.plus(FONT_SMALL_ADD).times(LINE_HEIGHT_MULTIPLIER).sp,
            ),
        titleMedium =
            Typography().titleMedium.copy(
                fontSize = baseFontSize.plus(FONT_MEDIUM_ADD).sp,
                lineHeight = baseFontSize.plus(FONT_MEDIUM_ADD).times(LINE_HEIGHT_MULTIPLIER).sp,
            ),
        titleLarge =
            Typography().titleLarge.copy(
                fontSize = baseFontSize.plus(FONT_LARGE_ADD).sp,
                lineHeight = baseFontSize.plus(FONT_LARGE_ADD).times(LINE_HEIGHT_MULTIPLIER).sp,
            ),
        headlineSmall =
            Typography().headlineSmall.copy(
                fontSize = baseFontSize.plus(FONT_SMALL_ADD).sp,
                lineHeight = baseFontSize.plus(FONT_SMALL_ADD).times(LINE_HEIGHT_MULTIPLIER).sp,
            ),
        headlineMedium =
            Typography().headlineMedium.copy(
                fontSize = baseFontSize.plus(FONT_MEDIUM_ADD).sp,
                lineHeight = baseFontSize.plus(FONT_MEDIUM_ADD).times(LINE_HEIGHT_MULTIPLIER).sp,
            ),
        headlineLarge =
            Typography().headlineLarge.copy(
                fontSize = baseFontSize.plus(FONT_LARGE_ADD).sp,
                lineHeight = baseFontSize.plus(FONT_LARGE_ADD).times(LINE_HEIGHT_MULTIPLIER).sp,
            ),
        labelSmall =
            Typography().labelSmall.copy(
                fontSize = baseFontSize.plus(FONT_SMALL_ADD).sp,
                lineHeight = baseFontSize.plus(FONT_SMALL_ADD).times(LINE_HEIGHT_MULTIPLIER).sp,
            ),
        labelMedium =
            Typography().labelMedium.copy(
                fontSize = baseFontSize.plus(FONT_MEDIUM_ADD).sp,
                lineHeight = baseFontSize.plus(FONT_MEDIUM_ADD).times(LINE_HEIGHT_MULTIPLIER).sp,
            ),
        labelLarge =
            Typography().labelLarge.copy(
                fontSize = baseFontSize.plus(FONT_LARGE_ADD).sp,
                lineHeight = baseFontSize.plus(FONT_LARGE_ADD).times(LINE_HEIGHT_MULTIPLIER).sp,
            ),
    )
}
