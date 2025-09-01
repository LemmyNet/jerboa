package com.jerboa.ui.components.settings.lookandfeel

import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.automirrored.outlined.ViewList
import androidx.compose.material.icons.outlined.Colorize
import androidx.compose.material.icons.outlined.FormatSize
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.LensBlur
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Swipe
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.core.os.LocaleListCompat
import com.jerboa.PostViewMode
import com.jerboa.R
import com.jerboa.ThemeColor
import com.jerboa.ThemeMode
import com.jerboa.db.APP_SETTINGS_DEFAULT
import com.jerboa.db.entity.AppSettings
import com.jerboa.feat.BackConfirmationMode
import com.jerboa.feat.BlurNSFW
import com.jerboa.feat.PostActionBarMode
import com.jerboa.feat.PostNavigationGestureMode
import com.jerboa.feat.SwipeToActionPreset
import com.jerboa.getLangPreferenceDropdownEntries
import com.jerboa.matchLocale
import com.jerboa.model.AppSettingsViewModel
import com.jerboa.ui.components.common.JerboaSnackbarHost
import com.jerboa.ui.components.common.SimpleTopAppBar
import me.zhanghai.compose.preference.ListPreference
import me.zhanghai.compose.preference.ListPreferenceType
import me.zhanghai.compose.preference.ProvidePreferenceTheme
import me.zhanghai.compose.preference.SliderPreference
import me.zhanghai.compose.preference.SwitchPreference

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LookAndFeelScreen(
    appSettingsViewModel: AppSettingsViewModel,
    onBack: () -> Unit,
) {
    Log.d("jerboa", "Got to lookAndFeel screen")
    val ctx = LocalContext.current

    val settings = appSettingsViewModel.appSettings.value ?: APP_SETTINGS_DEFAULT

    var themeState by remember { mutableStateOf(ThemeMode.entries[settings.theme]) }
    var themeColorState by remember { mutableStateOf(ThemeColor.entries[settings.themeColor]) }

    val localeMap =
        remember {
            getLangPreferenceDropdownEntries(ctx)
        }

    val currentAppLocale = matchLocale(localeMap)
    var fontSizeState by remember { mutableFloatStateOf(settings.fontSize.toFloat()) }
    var fontSizeSliderState by remember { mutableFloatStateOf(fontSizeState) }
    var postViewModeState by remember { mutableStateOf(PostViewMode.entries[settings.postViewMode]) }
    var postNavigationGestureModeState by remember { mutableStateOf(PostNavigationGestureMode.entries[settings.postNavigationGestureMode]) }
    var backConfirmationModeState by remember { mutableStateOf(BackConfirmationMode.entries[settings.backConfirmationMode]) }
    var postActionBarModeState by remember { mutableStateOf(PostActionBarMode.entries[settings.postActionBarMode]) }
    var blurNsfwState by remember { mutableStateOf(BlurNSFW.entries[settings.blurNSFW]) }
    var swipeToActionPresetState by remember { mutableStateOf(SwipeToActionPreset.entries[settings.swipeToActionPreset]) }

    var showBottomNavState by remember { mutableStateOf(settings.showBottomNav) }
    var showTextDescriptionsInNavbarState by remember { mutableStateOf(settings.showTextDescriptionsInNavbar) }
    var showCollapsedCommentContentState by remember { mutableStateOf(settings.showCollapsedCommentContent) }
    var showCommentActionBarByDefaultState by remember { mutableStateOf(settings.showCommentActionBarByDefault) }
    var showVotingArrowsInListViewState by remember { mutableStateOf(settings.showVotingArrowsInListView) }
    var showParentCommentNavigationButtonsState by remember { mutableStateOf(settings.showParentCommentNavigationButtons) }
    var navigateParentCommentsWithVolumeButtonsState by remember { mutableStateOf(settings.navigateParentCommentsWithVolumeButtons) }
    var useCustomTabsState by remember { mutableStateOf(settings.useCustomTabs) }
    var usePrivateTabsState by remember { mutableStateOf(settings.usePrivateTabs) }
    var secureWindowState by remember { mutableStateOf(settings.secureWindow) }
    var showPostLinkPreviewModeState by remember { mutableStateOf(settings.showPostLinkPreviews) }
    var markAsReadOnScrollState by remember { mutableStateOf(settings.markAsReadOnScroll) }
    var autoPlayGifsState by remember { mutableStateOf(settings.autoPlayGifs) }
    var disableVideoAutoplayState by remember { mutableStateOf(settings.disableVideoAutoplay == 1) }

    val snackbarHostState = remember { SnackbarHostState() }

    val scrollState = rememberScrollState()

    fun updateAppSettings() {
        appSettingsViewModel.update(
            AppSettings(
                id = 1,
                lastVersionCodeViewed = settings.lastVersionCodeViewed,
                theme = themeState.ordinal,
                themeColor = themeColorState.ordinal,
                fontSize = fontSizeState.toInt(),
                postViewMode = postViewModeState.ordinal,
                showBottomNav = showBottomNavState,
                showCollapsedCommentContent = showCollapsedCommentContentState,
                showCommentActionBarByDefault = showCommentActionBarByDefaultState,
                showVotingArrowsInListView = showVotingArrowsInListViewState,
                showParentCommentNavigationButtons = showParentCommentNavigationButtonsState,
                navigateParentCommentsWithVolumeButtons = navigateParentCommentsWithVolumeButtonsState,
                useCustomTabs = useCustomTabsState,
                usePrivateTabs = usePrivateTabsState,
                secureWindow = secureWindowState,
                showTextDescriptionsInNavbar = showTextDescriptionsInNavbarState,
                blurNSFW = blurNsfwState.ordinal,
                backConfirmationMode = backConfirmationModeState.ordinal,
                showPostLinkPreviews = showPostLinkPreviewModeState,
                markAsReadOnScroll = markAsReadOnScrollState,
                postActionBarMode = postActionBarModeState.ordinal,
                autoPlayGifs = autoPlayGifsState,
                postNavigationGestureMode = postNavigationGestureModeState.ordinal,
                swipeToActionPreset = swipeToActionPresetState.ordinal,
                disableVideoAutoplay = if (disableVideoAutoplayState) 1 else 0,
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
                modifier =
                    Modifier
                        .verticalScroll(scrollState)
                        .padding(padding),
            ) {
                ProvidePreferenceTheme {
                    ListPreference(
                        type = ListPreferenceType.DROPDOWN_MENU,
                        value = currentAppLocale.displayName,
                        onValueChange = { name ->
                            val entry = localeMap.entries.find { it.value == name }
                            AppCompatDelegate.setApplicationLocales(
                                LocaleListCompat.create(entry?.key),
                            )
                        },
                        values = localeMap.values.toList(),
                        title = {
                            Text(text = stringResource(R.string.lang_language))
                        },
                        summary = { Text(currentAppLocale.displayName) },
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.Language,
                                contentDescription = stringResource(R.string.lang_language),
                            )
                        },
                    )

                    SliderPreference(
                        value = fontSizeState,
                        sliderValue = fontSizeSliderState,
                        onValueChange = {
                            fontSizeState = it
                            updateAppSettings()
                        },
                        onSliderValueChange = { fontSizeSliderState = it },
                        valueRange = 8f..48f,
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.FormatSize,
                                contentDescription = null,
                            )
                        },
                        title = {
                            Text(
                                text =
                                    stringResource(
                                        R.string.look_and_feel_font_size,
                                        fontSizeSliderState.toInt(),
                                    ),
                            )
                        },
                    )
                    ListPreference(
                        type = ListPreferenceType.DROPDOWN_MENU,
                        value = themeState,
                        onValueChange = {
                            themeState = it
                            updateAppSettings()
                        },
                        values = ThemeMode.entries,
                        valueToText = {
                            AnnotatedString(ctx.getString(it.resId))
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.Palette,
                                contentDescription = null,
                            )
                        },
                        title = {
                            Text(text = stringResource(R.string.look_and_feel_theme))
                        },
                        summary = {
                            Text(stringResource(themeState.resId))
                        },
                    )

                    ListPreference(
                        type = ListPreferenceType.DROPDOWN_MENU,
                        value = themeColorState,
                        onValueChange = {
                            themeColorState = it
                            updateAppSettings()
                        },
                        values = ThemeColor.entries,
                        valueToText = {
                            AnnotatedString(ctx.getString(it.resId))
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.Colorize,
                                contentDescription = null,
                            )
                        },
                        title = {
                            Text(text = stringResource(R.string.look_and_feel_theme_color))
                        },
                        summary = {
                            Text(stringResource(themeColorState.resId))
                        },
                    )

                    ListPreference(
                        type = ListPreferenceType.DROPDOWN_MENU,
                        value = postViewModeState,
                        onValueChange = {
                            postViewModeState = it
                            updateAppSettings()
                        },
                        values = PostViewMode.entries,
                        valueToText = {
                            AnnotatedString(ctx.getString(it.resId))
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.ViewList,
                                contentDescription = null,
                            )
                        },
                        title = {
                            Text(text = stringResource(R.string.look_and_feel_post_view))
                        },
                        summary = {
                            Text(stringResource(postViewModeState.resId))
                        },
                    )

                    ListPreference(
                        type = ListPreferenceType.DROPDOWN_MENU,
                        value = postNavigationGestureModeState,
                        onValueChange = {
                            postNavigationGestureModeState = it
                            updateAppSettings()
                        },
                        values = PostNavigationGestureMode.entries,
                        valueToText = {
                            AnnotatedString(ctx.getString(it.resId))
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.Swipe,
                                contentDescription = null,
                            )
                        },
                        title = {
                            Text(text = stringResource(R.string.look_and_feel_post_navigation_gesture_mode))
                        },
                        summary = {
                            Text(stringResource(postNavigationGestureModeState.resId))
                        },
                    )

                    ListPreference(
                        type = ListPreferenceType.DROPDOWN_MENU,
                        value = backConfirmationModeState,
                        onValueChange = {
                            backConfirmationModeState = it
                            updateAppSettings()
                        },
                        values = BackConfirmationMode.entries,
                        valueToText = {
                            AnnotatedString(ctx.getString(it.resId))
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.ExitToApp,
                                contentDescription = null,
                            )
                        },
                        title = {
                            Text(text = stringResource(R.string.confirm_exit))
                        },
                        summary = {
                            Text(stringResource(backConfirmationModeState.resId))
                        },
                    )

                    ListPreference(
                        type = ListPreferenceType.DROPDOWN_MENU,
                        value = postActionBarModeState,
                        onValueChange = {
                            postActionBarModeState = it
                            updateAppSettings()
                        },
                        values = PostActionBarMode.entries,
                        valueToText = {
                            AnnotatedString(ctx.getString(it.resId))
                        },
                        title = {
                            Text(text = stringResource(R.string.post_actionbar))
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.Forum,
                                contentDescription = null,
                            )
                        },
                        summary = {
                            Text(stringResource(postActionBarModeState.resId))
                        },
                    )
                    ListPreference(
                        type = ListPreferenceType.DROPDOWN_MENU,
                        value = blurNsfwState,
                        onValueChange = {
                            blurNsfwState = it
                            updateAppSettings()
                        },
                        values = BlurNSFW.entries,
                        valueToText = {
                            AnnotatedString(ctx.getString(it.resId))
                        },
                        title = { Text(stringResource(id = R.string.blur_nsfw)) },
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.LensBlur,
                                contentDescription = null,
                            )
                        },
                        summary = {
                            Text(stringResource(blurNsfwState.resId))
                        },
                    )

                    ListPreference(
                        type = ListPreferenceType.DROPDOWN_MENU,
                        value = swipeToActionPresetState,
                        onValueChange = {
                            swipeToActionPresetState = it
                            updateAppSettings()
                        },
                        values = SwipeToActionPreset.entries,
                        valueToText = {
                            AnnotatedString(ctx.getString(it.resId))
                        },
                        title = { Text(stringResource(id = R.string.swipe_to_action_presets)) },
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.Swipe,
                                contentDescription = null,
                            )
                        },
                        summary = {
                            Text(stringResource(swipeToActionPresetState.resId))
                        },
                    )
                    SwitchPreference(
                        value = showBottomNavState,
                        onValueChange = {
                            showBottomNavState = it
                            updateAppSettings()
                        },
                        title = {
                            Text(text = stringResource(R.string.look_and_feel_show_navigation_bar))
                        },
                    )

                    SwitchPreference(
                        enabled = showBottomNavState,
                        value = showTextDescriptionsInNavbarState,
                        onValueChange = {
                            showTextDescriptionsInNavbarState = it
                            updateAppSettings()
                        },
                        title = {
                            Text(text = stringResource(R.string.look_and_feel_show_text_descriptions_in_navbar))
                        },
                    )

                    SwitchPreference(
                        value = showCollapsedCommentContentState,
                        onValueChange = {
                            showCollapsedCommentContentState = it
                            updateAppSettings()
                        },
                        title = {
                            Text(text = stringResource(R.string.look_and_feel_screen_show_content_for_collapsed_comments))
                        },
                    )

                    SwitchPreference(
                        value = showCommentActionBarByDefaultState,
                        onValueChange = {
                            showCommentActionBarByDefaultState = it
                            updateAppSettings()
                        },
                        title = {
                            Text(text = stringResource(R.string.look_and_feel_show_action_bar_for_comments))
                        },
                    )

                    SwitchPreference(
                        value = showVotingArrowsInListViewState,
                        onValueChange = {
                            showVotingArrowsInListViewState = it
                            updateAppSettings()
                        },
                        title = {
                            Text(text = stringResource(R.string.look_and_feel_show_voting_arrows_list_view))
                        },
                    )
                    SwitchPreference(
                        value = showParentCommentNavigationButtonsState,
                        onValueChange = {
                            showParentCommentNavigationButtonsState = it
                            updateAppSettings()
                        },
                        title = {
                            Text(text = stringResource(R.string.look_and_feel_show_parent_comment_navigation_buttons))
                        },
                    )
                    SwitchPreference(
                        value = navigateParentCommentsWithVolumeButtonsState,
                        onValueChange = {
                            navigateParentCommentsWithVolumeButtonsState = it
                            updateAppSettings()
                        },
                        title = {
                            Text(text = stringResource(R.string.look_and_feel_navigate_parent_comments_with_volume_buttons))
                        },
                    )
                    SwitchPreference(
                        value = useCustomTabsState,
                        onValueChange = {
                            useCustomTabsState = it
                            updateAppSettings()
                        },
                        title = {
                            Text(text = stringResource(id = R.string.look_and_feel_use_custom_tabs))
                        },
                    )
                    SwitchPreference(
                        value = usePrivateTabsState,
                        onValueChange = {
                            usePrivateTabsState = it
                            updateAppSettings()
                        },
                        title = {
                            Text(text = stringResource(id = R.string.look_and_feel_use_private_tabs))
                        },
                    )
                    SwitchPreference(
                        value = secureWindowState,
                        onValueChange = {
                            secureWindowState = it
                            updateAppSettings()
                        },
                        title = {
                            Text(text = stringResource(R.string.look_and_feel_secure_window))
                        },
                    )
                    SwitchPreference(
                        value = showPostLinkPreviewModeState,
                        onValueChange = {
                            showPostLinkPreviewModeState = it
                            updateAppSettings()
                        },
                        title = {
                            Text(stringResource(id = R.string.show_post_link_previews))
                        },
                    )
                    SwitchPreference(
                        value = markAsReadOnScrollState,
                        onValueChange = {
                            markAsReadOnScrollState = it
                            updateAppSettings()
                        },
                        title = {
                            Text(stringResource(id = R.string.mark_as_read_on_scroll))
                        },
                    )
                    SwitchPreference(
                        value = autoPlayGifsState,
                        onValueChange = {
                            autoPlayGifsState = it
                            updateAppSettings()
                        },
                        title = {
                            Text(stringResource(id = R.string.settings_autoplaygifs))
                        },
                    )
                    SwitchPreference(
                        value = disableVideoAutoplayState,
                        onValueChange = {
                            disableVideoAutoplayState = it
                            updateAppSettings()
                        },
                        title = {
                            Text(stringResource(id = R.string.settings_disable_video_autoplay))
                        },
                    )
                }
            }
        },
    )
}
