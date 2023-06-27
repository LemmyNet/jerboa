package com.jerboa.ui.components.settings.lookandfeel

import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Colorize
import androidx.compose.material.icons.outlined.FormatSize
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Tab
import androidx.compose.material.icons.outlined.ViewList
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import androidx.navigation.NavController
import com.alorma.compose.settings.storage.base.rememberBooleanSettingState
import com.alorma.compose.settings.storage.base.rememberFloatSettingState
import com.alorma.compose.settings.storage.base.rememberIntSettingState
import com.alorma.compose.settings.ui.SettingsCheckbox
import com.alorma.compose.settings.ui.SettingsList
import com.alorma.compose.settings.ui.SettingsListDropdown
import com.alorma.compose.settings.ui.SettingsSlider
import com.jerboa.PostViewMode
import com.jerboa.R
import com.jerboa.ThemeColor
import com.jerboa.ThemeMode
import com.jerboa.db.AppSettings
import com.jerboa.db.AppSettingsViewModel
import com.jerboa.db.DEFAULT_FONT_SIZE
import com.jerboa.getLangPreferenceDropdownEntries
import com.jerboa.matchLocale
import com.jerboa.ui.components.common.SimpleTopAppBar
import com.jerboa.ui.theme.ICON_SIZE
import com.jerboa.ui.theme.LARGE_PADDING
import com.jerboa.ui.theme.MEDIUM_PADDING
import com.jerboa.util.BrowserType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LookAndFeelActivity(
    navController: NavController,
    appSettingsViewModel: AppSettingsViewModel,
) {
    Log.d("jerboa", "Got to lookAndFeel activity")
    val ctx = LocalContext.current

    val settings = appSettingsViewModel.appSettings.value
    val themeState = rememberIntSettingState(settings?.theme ?: 0)
    val themeColorState = rememberIntSettingState(settings?.themeColor ?: 0)

    val localeMap = remember {
        getLangPreferenceDropdownEntries(ctx)
    }

    val currentAppLocale = matchLocale(localeMap)
    val langState = rememberIntSettingState(localeMap.keys.indexOf(currentAppLocale))

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
    val showParentCommentNavigationButtonsState = rememberBooleanSettingState(
        settings?.showParentCommentNavigationButtons ?: true,
    )
    val navigateParentCommentsWithVolumeButtonsState = rememberBooleanSettingState(
        settings?.navigateParentCommentsWithVolumeButtons ?: false,
    )
    val browserTypeState = rememberIntSettingState(settings?.browserType ?: 0)

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
                showParentCommentNavigationButtons = showParentCommentNavigationButtonsState.value,
                navigateParentCommentsWithVolumeButtons = navigateParentCommentsWithVolumeButtonsState.value,
                browserType = browserTypeState.value,
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
                SettingsListDropdown(
                    title = {
                        Text(text = stringResource(R.string.lang_language))
                    },
                    enabled = true,
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Language,
                            contentDescription = stringResource(R.string.lang_language),
                        )
                    },
                    state = langState,
                    items = localeMap.values.toList(),
                    onItemSelected = { i, _ ->
                        AppCompatDelegate.setApplicationLocales(
                            LocaleListCompat.create(localeMap.keys.elementAt(i)),
                        )
                    },
                )
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
                    items = ThemeColor.values().map { stringResource(it.mode) },
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
                    state = showParentCommentNavigationButtonsState,
                    title = {
                        Text(text = stringResource(R.string.look_and_feel_show_parent_comment_navigation_buttons))
                    },
                    onCheckedChange = { updateAppSettings() },
                )
                SettingsCheckbox(
                    state = navigateParentCommentsWithVolumeButtonsState,
                    title = {
                        Text(text = stringResource(R.string.look_and_feel_navigate_parent_comments_with_volume_buttons))
                    },
                    onCheckedChange = { updateAppSettings() },
                )
                SettingsList(
                    state = browserTypeState,
                    items = BrowserType.values().map { stringResource(it.resourceId) },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Tab,
                            contentDescription = null,
                        )
                    },
                    title = {
                        Text(text = stringResource(R.string.browserType_defaultBrowser))
                    },
                    onItemSelected = { i, _ ->
                        browserTypeState.value = BrowserType.values().find { it.id == i }?.id ?: 1
                        updateAppSettings()
                    },
                )
                AnimatedVisibility(
                    visible = browserTypeState.value == BrowserType.PrivateCustomTab.id,
                    modifier = Modifier.padding(LARGE_PADDING),

                ) {
                    Row {
                        Icon(
                            imageVector = Icons.Outlined.Warning,
                            contentDescription = null,
                            modifier = Modifier.padding(MEDIUM_PADDING).size(ICON_SIZE),
                        )
                        Text(
                            modifier = Modifier.padding(start = LARGE_PADDING),
                            text = stringResource(R.string.browserType_privateCustomTab_warning),
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
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
