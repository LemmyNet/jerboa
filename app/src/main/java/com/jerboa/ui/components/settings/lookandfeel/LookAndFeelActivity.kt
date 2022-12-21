package com.jerboa.ui.components.settings.lookandfeel

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.outlined.FlashlightOn
import androidx.compose.material.icons.outlined.FormatSize
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.ShieldMoon
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.alorma.compose.settings.storage.base.SettingValueState
import com.alorma.compose.settings.storage.base.rememberFloatSettingState
import com.alorma.compose.settings.storage.base.rememberIntSettingState
import com.alorma.compose.settings.ui.SettingsList
import com.alorma.compose.settings.ui.SettingsSlider
import com.jerboa.DarkTheme
import com.jerboa.LightTheme
import com.jerboa.ThemeMode
import com.jerboa.db.AppSettings
import com.jerboa.db.AppSettingsViewModel
import com.jerboa.db.DEFAULT_FONT_SIZE
import com.jerboa.ui.components.common.SimpleTopAppBar

@Composable
fun LookAndFeelActivity(
    navController: NavController,
    appSettingsViewModel: AppSettingsViewModel
) {
    Log.d("jerboa", "Got to lookAndFeel activity")

    val scaffoldState = rememberScaffoldState()

    val settings = appSettingsViewModel.appSettings.value
    val themeState = rememberIntSettingState(settings?.theme ?: 0)
    val lightThemeState = rememberIntSettingState(settings?.lightTheme ?: 0)
    val darkThemeState = rememberIntSettingState(settings?.darkTheme ?: 0)
    val fontSizeState = rememberFloatSettingState(
        settings?.fontSize?.toFloat()
            ?: DEFAULT_FONT_SIZE.toFloat()
    )

    Surface(color = MaterialTheme.colors.background) {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                SimpleTopAppBar(text = "Look and feel", navController = navController)
            },
            content = { padding ->
                Column(modifier = Modifier.padding(padding)) {
                    SettingsSlider(
                        modifier = Modifier.padding(top = 10.dp),
                        valueRange = 8f..48f,
                        state = fontSizeState,
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.FormatSize,
                                contentDescription = "TODO"
                            )
                        },
                        title = {
                            Text(text = "Font size: ${fontSizeState.value.toInt()}")
                        },
                        onValueChangeFinished = {
                            updateAppSettings(
                                appSettingsViewModel,
                                fontSizeState,
                                themeState,
                                lightThemeState,
                                darkThemeState
                            )
                        }
                    )
                    SettingsList(
                        state = themeState,
                        items = ThemeMode.values().map { it.name },
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.Palette,
                                contentDescription = "TODO"
                            )
                        },
                        title = {
                            Text(text = "Theme")
                        },
                        action = {
                            updateAppSettings(
                                appSettingsViewModel,
                                fontSizeState,
                                themeState,
                                lightThemeState,
                                darkThemeState
                            )
                        }
                    )
                    SettingsList(
                        state = lightThemeState,
                        items = LightTheme.values().map { it.name },
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.FlashlightOn,
                                contentDescription = "TODO"
                            )
                        },
                        title = {
                            Text(text = "Light theme")
                        },
                        action = {
                            updateAppSettings(
                                appSettingsViewModel,
                                fontSizeState,
                                themeState,
                                lightThemeState,
                                darkThemeState
                            )
                        }
                    )
                    SettingsList(
                        state = darkThemeState,
                        items = DarkTheme.values().map { it.name },
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.ShieldMoon,
                                contentDescription = "TODO"
                            )
                        },
                        title = {
                            Text(text = "Dark theme")
                        },
                        action = {
                            updateAppSettings(
                                appSettingsViewModel,
                                fontSizeState,
                                themeState,
                                lightThemeState,
                                darkThemeState
                            )
                        }
                    )
                }
            }
        )
    }
}

private fun updateAppSettings(
    appSettingsViewModel: AppSettingsViewModel,
    fontSizeState: SettingValueState<Float>,
    themeState: SettingValueState<Int>,
    lightThemeState: SettingValueState<Int>,
    darkThemeState: SettingValueState<Int>
) {
    appSettingsViewModel.update(
        AppSettings(
            id = 1,
            fontSize = fontSizeState.value.toInt(),
            theme = themeState.value,
            lightTheme = lightThemeState.value,
            darkTheme = darkThemeState.value,
            viewedChangelog = appSettingsViewModel.appSettings.value?.viewedChangelog ?: 0
        )
    )
}
