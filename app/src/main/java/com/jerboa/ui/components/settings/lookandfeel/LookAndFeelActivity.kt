package com.jerboa.ui.components.settings.lookandfeel

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.alorma.compose.settings.storage.base.rememberBooleanSettingState
import com.alorma.compose.settings.storage.base.rememberFloatSettingState
import com.alorma.compose.settings.storage.base.rememberIntSettingState
import com.alorma.compose.settings.ui.SettingsCheckbox
import com.alorma.compose.settings.ui.SettingsList
import com.alorma.compose.settings.ui.SettingsSlider
import com.jerboa.PostViewMode
import com.jerboa.R
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
    val showCommentActionBarByDefaultState = rememberBooleanSettingState(
        settings?.showCommentActionBarByDefault ?: true,
    )
    val showVotingArrowsInListViewState = rememberBooleanSettingState(
        settings?.showVotingArrowsInListView ?: true,
    )
    val useCustomTabsState = rememberBooleanSettingState(settings?.useCustomTabs ?: true)
    val usePrivateTabsState = rememberBooleanSettingState(settings?.usePrivateTabs ?: false)

    val secureWindowState = rememberBooleanSettingState(settings?.secureWindow ?: false)

    val snackbarHostState = remember { SnackbarHostState() }
	
    val scrollState = rememberScrollState()

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
                showCommentActionBarByDefault = showCommentActionBarByDefaultState.value,
                showVotingArrowsInListView = showVotingArrowsInListViewState.value,
                useCustomTabs = useCustomTabsState.value,
                usePrivateTabs = usePrivateTabsState.value,
                secureWindow = secureWindowState.value,
            ),
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            SimpleTopAppBar(text = stringResource(R.string.look_and_feel_look_and_feel), navController = navController)
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .padding(padding),
            ) {
                SettingsSlider(
                    modifier = Modifier.padding(top = 10.dp),
                    valueRange = 8f..48f,
                    state = fontSizeState,
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.FormatSize,
                            contentDescription = null,
                        )
                    },
                    title = {
                        Text(
                            text = stringResource(
                                R.string.look_and_feel_font_size,
                                fontSizeState.value.toInt(),
                            ),
                        )
                    },
                    onValueChangeFinished = { updateAppSettings() },
                )
                SettingsList(
                    state = themeState,
                    items = ThemeMode.values().map { stringResource(it.mode) },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Palette,
                            contentDescription = null,
                        )
                    },
                    title = {
                        Text(text = stringResource(R.string.look_and_feel_theme))
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
                            contentDescription = null,
                        )
                    },
                    title = {
                        Text(text = stringResource(R.string.look_and_feel_theme_color))
                    },
                    onItemSelected = { i, _ ->
                        themeColorState.value = i
                        updateAppSettings()
                    },
                )
                SettingsList(
                    state = postViewModeState,
                    items = PostViewMode.values().map { stringResource(it.mode) },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.ViewList,
                            contentDescription = null,
                        )
                    },
                    title = {
                        Text(text = stringResource(R.string.look_and_feel_post_view))
                    },
                    onItemSelected = { i, _ ->
                        postViewModeState.value = i
                        updateAppSettings()
                    },
                )
                SettingsCheckbox(
                    state = showBottomNavState,
                    title = {
                        Text(text = stringResource(R.string.look_and_feel_show_navigation_bar))
                    },
                    onCheckedChange = { updateAppSettings() },
                )
                SettingsCheckbox(
                    state = showCollapsedCommentContentState,
                    title = {
                        Text(text = stringResource(R.string.look_and_feel_activity_show_content_for_collapsed_comments))
                    },
                    onCheckedChange = { updateAppSettings() },
                )
                SettingsCheckbox(
                    state = showCommentActionBarByDefaultState,
                    title = {
                        Text(text = stringResource(R.string.look_and_feel_show_action_bar_for_comments))
                    },
                    onCheckedChange = { updateAppSettings() },
                )
                SettingsCheckbox(
                    state = showVotingArrowsInListViewState,
                    title = {
                        Text(text = stringResource(R.string.look_and_feel_show_voting_arrows_list_view))
                    },
                    onCheckedChange = { updateAppSettings() },
                )
                SettingsCheckbox(
                    state = useCustomTabsState,
                    title = {
                        Text(text = stringResource(id = R.string.look_and_feel_use_custom_tabs))
                    },
                    onCheckedChange = { updateAppSettings() },
                )
                SettingsCheckbox(
                    state = usePrivateTabsState,
                    title = {
                        Text(text = stringResource(id = R.string.look_and_feel_use_private_tabs))
                    },
                    onCheckedChange = { updateAppSettings() },
                )
                SettingsCheckbox(
                    state = secureWindowState,
                    title = {
                        Text(text = stringResource(R.string.look_and_feel_secure_window))
                    },
                    onCheckedChange = { updateAppSettings() },
                )
            }
        },
    )
}
