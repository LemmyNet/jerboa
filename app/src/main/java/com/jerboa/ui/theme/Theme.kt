package com.jerboa.ui.theme

import android.app.Activity
import android.os.Build
import android.view.WindowManager
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.sp
import androidx.core.graphics.ColorUtils
import androidx.core.view.WindowCompat
import com.jerboa.ThemeColor
import com.jerboa.ThemeMode
import com.jerboa.db.entity.AppSettings

// Defines a provider for custom color scheme use; initializes it to a default scheme that will
// be overridden by JerboaTheme
private val LocalColorScheme = staticCompositionLocalOf { JerboaColorScheme() }

@Composable
fun JerboaTheme(
    appSettings: AppSettings,
    content: @Composable () -> Unit,
) {
    val themeMode = ThemeMode.entries[appSettings.theme]
    val themeColor = ThemeColor.entries[appSettings.themeColor]
    val fontSize = appSettings.fontSize.sp

    val ctx = LocalContext.current
    val android12OrLater = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    // Dynamic schemes crash on lower than android 12
    val dynamicPair = if (android12OrLater) {
        val jerboaImageHighlight = Color(0xCCD1D1D1)
        val jerboaVideoHighlight = Color(0xCCC20000)
        Pair(
            JerboaColorScheme(
                material = dynamicLightColorScheme(ctx),
                videoHighlight = jerboaVideoHighlight,
                imageHighlight = jerboaImageHighlight,
            ),
            JerboaColorScheme(
                material = dynamicDarkColorScheme(ctx),
                videoHighlight = jerboaVideoHighlight,
                imageHighlight = jerboaImageHighlight,
            ),
        )
    } else {
        pink()
    }

    val colorPair = when (themeColor) {
        ThemeColor.Dynamic -> dynamicPair
        ThemeColor.Beach -> beach()
        ThemeColor.Blue -> blue()
        ThemeColor.Crimson -> crimson()
        ThemeColor.Green -> green()
        ThemeColor.Grey -> grey()
        ThemeColor.Pink -> pink()
        ThemeColor.Purple -> purple()
        ThemeColor.Woodland -> woodland()
    }

    fun makeBlack(darkTheme: JerboaColorScheme): JerboaColorScheme {
        return darkTheme.copy(
            material = darkTheme.material.copy(
                background = Color(0xFF000000),
                surface = Color(0xFF000000),
            ),
        )
    }

    val systemTheme = if (!isSystemInDarkTheme()) {
        colorPair.first
    } else {
        if (themeMode == ThemeMode.SystemBlack) {
            makeBlack(colorPair.second)
        } else {
            colorPair.second
        }
    }

    val colors = when (themeMode) {
        ThemeMode.System -> systemTheme
        ThemeMode.SystemBlack -> systemTheme
        ThemeMode.Light -> colorPair.first
        ThemeMode.Dark -> colorPair.second
        ThemeMode.Black -> makeBlack(colorPair.second)
    }

    val typography = generateTypography(fontSize)

    val view = LocalView.current

    val window = (view.context as Activity).window
    val insets = WindowCompat.getInsetsController(window, view)

    val isLight = when (themeMode) {
        ThemeMode.Black, ThemeMode.Dark -> false
        ThemeMode.System, ThemeMode.SystemBlack -> !isSystemInDarkTheme()
        else -> true
    }

    if (appSettings.secureWindow) {
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
    } else {
        window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
    }

    window.statusBarColor = colors.material.background.toArgb()
    // The navigation bar color is also set on BottomAppBarAll
    window.navigationBarColor = colors.material.background.toArgb()

    insets.isAppearanceLightStatusBars = isLight
    insets.isAppearanceLightNavigationBars = isLight

    // Set up a provider to allow access to the custom color scheme from any child element
    CompositionLocalProvider(LocalColorScheme provides colors) {
        // Set up the default MaterialTheme provider
        MaterialTheme(
            colorScheme = colors.material,
            typography = typography,
            shapes = Shapes,
            content = content,
        )
    }
}

/**
 * Enables access to the custom @see JerboaColorScheme instance for this @see MaterialTheme
 */
@Suppress("UnusedReceiverParameter") // Fix compiler complaining about not using `this`
val MaterialTheme.jerboaColorScheme: JerboaColorScheme
    @Composable
    @ReadOnlyComposable
    get() = LocalColorScheme.current

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
