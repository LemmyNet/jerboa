package com.jerboa.ui.components.settings.lookandfeel

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.alorma.compose.settings.storage.base.rememberBooleanSettingState
import com.alorma.compose.settings.storage.base.rememberFloatSettingState
import com.alorma.compose.settings.storage.base.rememberIntSettingState
import com.alorma.compose.settings.ui.SettingsCheckbox
import com.alorma.compose.settings.ui.SettingsList
import com.alorma.compose.settings.ui.SettingsSlider
import com.jerboa.PostViewMode
import com.jerboa.ThemeColor
import com.jerboa.ThemeMode
import com.jerboa.db.AppSettings
import com.jerboa.db.AppSettingsViewModel
import com.jerboa.db.DEFAULT_FONT_SIZE
import com.jerboa.ui.components.common.SimpleTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LookAndFeelActivity(
    navController: NavController,
    appSettingsViewModel: AppSettingsViewModel,
) {
    Log.d("jerboa", "Got to lookAndFeel activity")

    val settings = appSettingsViewModel.appSettings.value
    val themeState = rememberIntSettingState(settings?.theme ?: 0)
    val themeColorState = rememberIntSettingState(settings?.themeColor ?: 0)
    val fontSizeState = rememberFloatSettingState(
        settings?.fontSize?.toFloat()
            ?: DEFAULT_FONT_SIZE.toFloat(),
    )
    val postViewModeState = rememberIntSettingState(settings?.postViewMode ?: 0)
    val showBottomNavState = rememberBooleanSettingState(settings?.showBottomNav ?: true)
    val showCollapsedCommentContentState =
        rememberBooleanSettingState(settings?.showCollapsedCommentContent ?: false)

    val snackbarHostState = remember { SnackbarHostState() }

    fun updateAppSettings() {
        appSettingsViewModel.update(
            AppSettings(
                id = 1,
                viewedChangelog = appSettingsViewModel.appSettings.value?.viewedChangelog ?: 0,
                theme = themeState.value,
                themeColor = themeColorState.value,
                fontSize = fontSizeState.value.toInt(),
                postViewMode = postViewModeState.value,
                showBottomNav = showBottomNavState.value,
                showCollapsedCommentContent = showCollapsedCommentContentState.value,
            ),
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                            contentDescription = "TODO",
                        )
                    },
                    title = {
                        Text(text = "Font size: ${fontSizeState.value.toInt()}")
                    },
                    onValueChangeFinished = { updateAppSettings() },
                )
                SettingsList(
                    state = themeState,
                    items = ThemeMode.values().map { it.name },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Palette,
                            contentDescription = "TODO",
                        )
                    },
                    title = {
                        Text(text = "Theme")
                    },
                    onItemSelected = { i, _ ->
                        themeState.value = i
                        updateAppSettings()
                    },
                )
                SettingsList(
                    state = themeColorState,
                    items = ThemeColor.values().map { it.name },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Colorize,
                            contentDescription = "TODO",
                        )
                    },
                    title = {
                        Text(text = "Theme color")
                    },
                    onItemSelected = { i, _ ->
                        themeColorState.value = i
                        updateAppSettings()
                    },
                )
                SettingsList(
                    state = postViewModeState,
                    items = PostViewMode.values().map { it.mode },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.ViewList,
                            contentDescription = "TODO",
                        )
                    },
                    title = {
                        Text(text = "Post View")
                    },
                    onItemSelected = { i, _ ->
                        postViewModeState.value = i
                        updateAppSettings()
                    },
                )
                SettingsCheckbox(
                    state = showBottomNavState,
                    title = {
                        Text(text = "Show navigation bar")
                    },
                    onCheckedChange = { updateAppSettings() },
                )
                SettingsCheckbox(
                    state = showCollapsedCommentContentState,
                    title = {
                        Text(text = "Show content for collapsed comments")
                    },
                    onCheckedChange = { updateAppSettings() },
                )
            }
        },
    )
}
