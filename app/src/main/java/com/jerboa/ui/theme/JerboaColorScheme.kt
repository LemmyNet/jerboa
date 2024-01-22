package com.jerboa.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

/**
 * Provides custom Jerboa colors in addition to the default Material colors.
 */
data class JerboaColorScheme(
    val material: ColorScheme = darkColorScheme(), // the default Material color scheme
    val imageHighlight: Color = Color(0xCCD1D1D1), // the color that highlights an image thumb
    val videoHighlight: Color = Color(0xCCC20000), // the color that highlights a video thumb
)
