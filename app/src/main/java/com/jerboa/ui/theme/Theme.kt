package com.jerboa.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.core.graphics.ColorUtils
import com.jerboa.ThemeColor
import com.jerboa.ThemeMode
import com.jerboa.db.AppSettings
import com.jerboa.db.DEFAULT_FONT_SIZE

@Composable
fun JerboaTheme(
    appSettings: AppSettings?,
    content: @Composable () -> Unit
) {
    val themeMode = ThemeMode.values()[appSettings?.theme ?: 0]
    val themeColor = ThemeColor.values()[appSettings?.themeColor ?: 0]
    val fontSize = (appSettings?.fontSize ?: DEFAULT_FONT_SIZE).sp

    val ctx = LocalContext.current
    val dynamicLight = dynamicLightColorScheme(ctx)
    val dynamicDark = dynamicDarkColorScheme(ctx)

    val colorPair = when (themeColor) {
        ThemeColor.Dynamic -> Pair(dynamicLight, dynamicDark)
        ThemeColor.Green -> green()
        ThemeColor.Pink -> pink()
    }

    val systemTheme = if (!isSystemInDarkTheme()) {
        colorPair.first
    } else {
        colorPair.second
    }

    val colors = when (themeMode) {
        ThemeMode.System -> systemTheme
        ThemeMode.Light -> colorPair.first
        ThemeMode.Dark -> colorPair.second
    }

    val typography = generateTypography(fontSize)

    MaterialTheme(
        colorScheme = colors,
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
