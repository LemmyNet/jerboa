package com.jerboa.ui.components.settings.lookandfeel

import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Colorize
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material.icons.outlined.FormatSize
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.ViewList
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
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
import com.jerboa.db.APP_SETTINGS_DEFAULT
import com.jerboa.db.entity.AppSettings
import com.jerboa.feat.BackConfirmationMode
import com.jerboa.feat.PostActionbarMode
import com.jerboa.getLangPreferenceDropdownEntries
import com.jerboa.matchLocale
import com.jerboa.model.AppSettingsViewModel
import com.jerboa.ui.components.common.JerboaSnackbarHost
import com.jerboa.ui.components.common.SimpleTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LookAndFeelActivity(
    appSettingsViewModel: AppSettingsViewModel,
    onBack: () -> Unit,
) {
    Log.d("jerboa", "Got to lookAndFeel activity")
    val ctx = LocalContext.current

    val settings = appSettingsViewModel.appSettings.value ?: APP_SETTINGS_DEFAULT
    val themeState = rememberIntSettingState(settings.theme)
    val themeColorState = rememberIntSettingState(settings.themeColor)

    val localeMap = remember {
        getLangPreferenceDropdownEntries(ctx)
    }

    val currentAppLocale = matchLocale(localeMap)
    val langState = rememberIntSettingState(localeMap.keys.indexOf(currentAppLocale))

    val fontSizeState = rememberFloatSettingState(
        settings.fontSize.toFloat(),
    )
    val postViewModeState = rememberIntSettingState(settings.postViewMode)
    val showBottomNavState = rememberBooleanSettingState(settings.showBottomNav)
    val showTextDescriptionsInNavbar = rememberBooleanSettingState(settings.showTextDescriptionsInNavbar)
    val showCollapsedCommentContentState = rememberBooleanSettingState(settings.showCollapsedCommentContent)
    val showCommentActionBarByDefaultState = rememberBooleanSettingState(settings.showCommentActionBarByDefault)
    val showVotingArrowsInListViewState = rememberBooleanSettingState(settings.showVotingArrowsInListView)
    val showParentCommentNavigationButtonsState = rememberBooleanSettingState(
        settings.showParentCommentNavigationButtons,
    )
    val navigateParentCommentsWithVolumeButtonsState = rememberBooleanSettingState(
        settings.navigateParentCommentsWithVolumeButtons,
    )
    val useCustomTabsState = rememberBooleanSettingState(settings.useCustomTabs)
    val usePrivateTabsState = rememberBooleanSettingState(settings.usePrivateTabs)

    val secureWindowState = rememberBooleanSettingState(settings.secureWindow)
    val blurNSFW = rememberBooleanSettingState(settings.blurNSFW)
    val backConfirmationMode = rememberIntSettingState(settings.backConfirmationMode)
    val showPostLinkPreviewMode = rememberBooleanSettingState(settings.showPostLinkPreviews)
    val postActionbarMode = rememberIntSettingState(settings.postActionbarMode)

    val snackbarHostState = remember { SnackbarHostState() }

    val scrollState = rememberScrollState()

    val markAsReadOnScroll = rememberBooleanSettingState(settings.markAsReadOnScroll)
    val autoPlayGifs = rememberBooleanSettingState(settings.autoPlayGifs)

    fun updateAppSettings() {
        appSettingsViewModel.update(
            AppSettings(
                id = 1,
                viewedChangelog = settings.viewedChangelog,
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
                useCustomTabs = useCustomTabsState.value,
                usePrivateTabs = usePrivateTabsState.value,
                secureWindow = secureWindowState.value,
                showTextDescriptionsInNavbar = showTextDescriptionsInNavbar.value,
                blurNSFW = blurNSFW.value,
                backConfirmationMode = backConfirmationMode.value,
                showPostLinkPreviews = showPostLinkPreviewMode.value,
                markAsReadOnScroll = markAsReadOnScroll.value,
                postActionbarMode = postActionbarMode.value,
                autoPlayGifs = autoPlayGifs.value,
            ),
        )
    }

    Scaffold(
        snackbarHost = { JerboaSnackbarHost(snackbarHostState) },
        topBar = {
            SimpleTopAppBar(text = stringResource(R.string.look_and_feel_look_and_feel), onClickBack = onBack)
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
                    items = ThemeMode.entries.map { stringResource(it.mode) },
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
                    items = ThemeColor.entries.map { stringResource(it.mode) },
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
                    items = PostViewMode.entries.map { stringResource(it.mode) },
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
                SettingsList(
                    title = {
                        Text(text = stringResource(R.string.confirm_exit))
                    },
                    state = backConfirmationMode,
                    items = BackConfirmationMode.entries.map { stringResource(it.resId) },
                    onItemSelected = { i, _ ->
                        backConfirmationMode.value = i
                        updateAppSettings()
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.ExitToApp,
                            contentDescription = null,
                        )
                    },
                )
                SettingsList(
                    title = {
                        Text(text = stringResource(R.string.post_actionbar))
                    },
                    state = postActionbarMode,
                    items = PostActionbarMode.entries.map { stringResource(it.resId) },
                    onItemSelected = { i, _ ->
                        postActionbarMode.value = i
                        updateAppSettings()
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Forum,
                            contentDescription = null,
                        )
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
                    state = showTextDescriptionsInNavbar,
                    title = {
                        Text(text = stringResource(R.string.look_and_feel_show_text_descriptions_in_navbar))
                    },
                    onCheckedChange = { updateAppSettings() },
                    enabled = showBottomNavState.value,
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
                SettingsCheckbox(
                    state = blurNSFW,
                    title = {
                        Text(stringResource(id = R.string.blur_nsfw))
                    },
                    onCheckedChange = { updateAppSettings() },
                )
                SettingsCheckbox(
                    state = showPostLinkPreviewMode,
                    title = {
                        Text(stringResource(id = R.string.show_post_link_previews))
                    },
                    onCheckedChange = { updateAppSettings() },
                )
                SettingsCheckbox(
                    state = markAsReadOnScroll,
                    title = {
                        Text(stringResource(id = R.string.mark_as_read_on_scroll))
                    },
                    onCheckedChange = { updateAppSettings() },
                )
                SettingsCheckbox(
                    state = autoPlayGifs,
                    title = {
                        Text(stringResource(id = R.string.settings_autoplaygifs))
                    },
                    onCheckedChange = { updateAppSettings() },
                )
            }
        },
    )
}
