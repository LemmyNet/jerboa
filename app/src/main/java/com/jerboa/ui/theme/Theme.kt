package com.jerboa.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.core.graphics.ColorUtils
import com.jerboa.DarkTheme
import com.jerboa.LightTheme
import com.jerboa.ThemeMode
import com.jerboa.db.AppSettings
import com.jerboa.db.DEFAULT_FONT_SIZE

private val DarkGrayColorPalette = darkColors(
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

private val LightGreenPalette = lightColors(
    primary = Green200,
    primaryVariant = Green700,
    secondary = Blue200
)

private val LightPinkPalette = lightColors(
    primary = Pink200,
    primaryVariant = Pink700,
    secondary = Blue200
)

/* Other default colors to override
  background = Color.White,
  surface = Color.White,
  onPrimary = Color.White,
  onSecondary = Color.Black,
  onBackground = Color.Black,
  onSurface = Color.Black,
  */

@Composable
fun JerboaTheme(
    appSettings: AppSettings?,
    content: @Composable () -> Unit
) {
    val themeMode = ThemeMode.values()[appSettings?.theme ?: 0]
    val lightTheme = LightTheme.values()[appSettings?.lightTheme ?: 0]
    val darkTheme = DarkTheme.values()[appSettings?.darkTheme ?: 0]
    val fontSize = (appSettings?.fontSize ?: DEFAULT_FONT_SIZE).sp

    val darkThemeColors = when (darkTheme) {
        DarkTheme.Gray -> DarkGrayColorPalette
        DarkTheme.Blue -> DarkBlueColorPalette
        DarkTheme.Black -> BlackColorPalette
    }
    val lightThemeColors = when (lightTheme) {
        LightTheme.Green -> LightGreenPalette
        LightTheme.Pink -> LightPinkPalette
    }

    val systemTheme = if (isSystemInDarkTheme()) {
        darkThemeColors
    } else {
        lightThemeColors
    }

    val colors = when (themeMode) {
        ThemeMode.System -> systemTheme
        ThemeMode.Light -> lightThemeColors
        ThemeMode.Dark -> darkThemeColors
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
