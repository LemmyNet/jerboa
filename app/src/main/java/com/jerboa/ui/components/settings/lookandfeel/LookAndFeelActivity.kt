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
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.alorma.compose.settings.storage.base.SettingValueState
import com.alorma.compose.settings.storage.base.rememberFloatSettingState
import com.alorma.compose.settings.storage.base.rememberIntSettingState
import com.alorma.compose.settings.ui.SettingsList
import com.alorma.compose.settings.ui.SettingsSlider
import com.jerboa.ThemeMode
import com.jerboa.db.AppSettings
import com.jerboa.db.AppSettingsViewModel
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
    val fontSizeState = rememberFloatSettingState(settings?.fontSize?.toFloat() ?: 13f)

    Surface(color = MaterialTheme.colors.background) {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                SimpleTopAppBar(text = "Look and feel", navController = navController)
            },
            content = { padding ->
                Column(modifier = Modifier.padding(padding)) {
                    SettingsList(
                        state = themeState,
                        items = ThemeMode.values().map { it.name },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = "TODO"
                            )
                        },
                        title = {
                            Text(text = "Theme")
                        },
                        action = {
                            updateAppSettings(appSettingsViewModel, fontSizeState, themeState)
                        }
                    )
                    SettingsSlider(
                        valueRange = 8f..48f,
                        state = fontSizeState,
                        icon = {
                            Icon(
                                imageVector = Icons.Default.FormatSize,
                                contentDescription = "TODO"
                            )
                        },
                        title = {
                            Text(text = "Font size: ${fontSizeState.value.toInt()}")
                        },
                        onValueChangeFinished = {
                            updateAppSettings(appSettingsViewModel, fontSizeState, themeState)
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
    themeState: SettingValueState<Int>
) {
    appSettingsViewModel.update(
        AppSettings(
            1,
            fontSizeState.value.toInt(),
            themeState.value
        )
    )
}
