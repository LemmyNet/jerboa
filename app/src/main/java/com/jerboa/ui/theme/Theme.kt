package com.jerboa.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.core.graphics.ColorUtils
import com.jerboa.ThemeMode

private val DarkColorPalette = darkColors(
    primary = Green200,
    primaryVariant = Green700,
    secondary = Blue200
)

private val DarkBlueColorPalette = darkColors(
    primary = Green200,
    primaryVariant = Green700,
    secondary = Blue200,
    background = DarkSurfaceBlue,
    surface = DarkSurfaceBlue
)

private val BlackColorPalette = darkColors(
    primary = Green200,
    primaryVariant = Green700,
    secondary = Blue200,
    background = BlackSurface,
    surface = BlackSurface
)

private val LightColorPalette = lightColors(
    primary = Green200,
    primaryVariant = Green700,
    secondary = Blue200

  /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun JerboaTheme(
    themeMode: ThemeMode,
    fontSize: TextUnit,
    content: @Composable () -> Unit
) {
    val systemTheme = if (isSystemInDarkTheme()) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    val colors = when (themeMode) {
        ThemeMode.System -> systemTheme
        ThemeMode.Light -> LightColorPalette
        ThemeMode.Dark -> DarkColorPalette
        ThemeMode.DarkBlue -> DarkBlueColorPalette
        ThemeMode.Black -> BlackColorPalette
    }

    val typography = generateTypography(fontSize)

    MaterialTheme(
        colors = colors,
        typography = typography,
        shapes = Shapes,
        content = content
    )
}

val colorList = listOf(
    hsl(0f),
    hsl(100f),
    hsl(150f),
    hsl(200f),
    hsl(250f),
    hsl(300f)
)

fun hsl(num: Float): Color {
    return Color(ColorUtils.HSLToColor(floatArrayOf(num, .35f, .5f)))
}
