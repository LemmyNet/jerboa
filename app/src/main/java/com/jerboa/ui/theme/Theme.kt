package com.jerboa.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.sp
import androidx.core.graphics.ColorUtils
import androidx.core.view.WindowCompat
import com.jerboa.ThemeColor
import com.jerboa.ThemeMode
import com.jerboa.db.AppSettings
import com.jerboa.db.DEFAULT_FONT_SIZE

@Composable
fun JerboaTheme(
    appSettings: AppSettings?,
    content: @Composable () -> Unit,
) {
    val themeMode = ThemeMode.values()[appSettings?.theme ?: 0]
    val themeColor = ThemeColor.values()[appSettings?.themeColor ?: 0]
    val fontSize = (appSettings?.fontSize ?: DEFAULT_FONT_SIZE).sp

    val ctx = LocalContext.current
    val android12OrLater = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    // Dynamic schemes crash on lower than android 12
    val dynamicPair = if (android12OrLater) {
        Pair(dynamicLightColorScheme(ctx), dynamicDarkColorScheme(ctx))
    } else {
        pink()
    }

    val colorPair = when (themeColor) {
        ThemeColor.Dynamic -> dynamicPair
        ThemeColor.Green -> green()
        ThemeColor.Pink -> pink()
        ThemeColor.Blue -> blue()
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

    val view = LocalView.current

    if (isSystemInDarkTheme()) {
        SideEffect {
            val window = (view.context as Activity).window
            val insets = WindowCompat.getInsetsController(window, view)
            insets.isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colors,
        typography = typography,
        shapes = Shapes,
        content = content,
    )
}

val colorList = listOf(
    hsl(0f),
    hsl(100f),
    hsl(150f),
    hsl(200f),
    hsl(250f),
    hsl(300f),
)

fun hsl(num: Float): Color {
    return Color(ColorUtils.HSLToColor(floatArrayOf(num, .35f, .5f)))
}
