
package com.jerboa.ui.components.settings.lookandfeel

import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Divider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import com.alorma.compose.settings.storage.base.rememberIntSettingState
import com.alorma.compose.settings.ui.SettingsList
import com.alorma.compose.settings.ui.SettingsListDropdown
import com.jerboa.R
import com.jerboa.getLangPreferenceDropdownEntries
import com.jerboa.matchLocale
import com.jerboa.model.AppSettingsViewModel
import com.jerboa.db.APP_SETTINGS_DEFAULT
import com.jerboa.db.entity.AppSettings
import com.jerboa.ThemeColor
import com.jerboa.ThemeMode
import com.alorma.compose.settings.storage.base.rememberBooleanSettingState
import com.alorma.compose.settings.storage.base.rememberFloatSettingState
import com.alorma.compose.settings.storage.base.rememberIntSettingState
import com.alorma.compose.settings.ui.SettingsCheckbox
import com.jerboa.ui.components.common.SimpleTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityActivity(
    onBack: () -> Unit,
    onClickCrashLogs: () -> Unit,
    appSettingsViewModel: AppSettingsViewModel,
) {
    Log.d("jerboa", "Got to About activity")

    val ctx = LocalContext.current

    val settings = appSettingsViewModel.appSettings.value ?: APP_SETTINGS_DEFAULT
    val themeState = rememberIntSettingState(settings.theme)
    val themeColorState = rememberIntSettingState(settings.themeColor)

    val localeMap =
        remember {
            getLangPreferenceDropdownEntries(ctx)
        }

    val currentAppLocale = matchLocale(localeMap)
    val langState = rememberIntSettingState(localeMap.keys.indexOf(currentAppLocale))

    val fontSizeState =
        rememberFloatSettingState(
            settings.fontSize.toFloat(),
        )
    val postViewModeState = rememberIntSettingState(settings.postViewMode)
    val postNavigationGestureModeState = rememberIntSettingState(settings.postNavigationGestureMode)
    val showBottomNavState = rememberBooleanSettingState(settings.showBottomNav)
    val showTextDescriptionsInNavbar = rememberBooleanSettingState(settings.showTextDescriptionsInNavbar)
    val showCollapsedCommentContentState = rememberBooleanSettingState(settings.showCollapsedCommentContent)
    val showCommentActionBarByDefaultState = rememberBooleanSettingState(settings.showCommentActionBarByDefault)
    val showVotingArrowsInListViewState = rememberBooleanSettingState(settings.showVotingArrowsInListView)
    val showParentCommentNavigationButtonsState =
        rememberBooleanSettingState(
            settings.showParentCommentNavigationButtons,
        )
    val navigateParentCommentsWithVolumeButtonsState =
        rememberBooleanSettingState(
            settings.navigateParentCommentsWithVolumeButtons,
        )
    val useCustomTabsState = rememberBooleanSettingState(settings.useCustomTabs)
    val usePrivateTabsState = rememberBooleanSettingState(settings.usePrivateTabs)

    val secureWindowState = rememberBooleanSettingState(settings.secureWindow)
    val blurNSFW = rememberIntSettingState(settings.blurNSFW)
    val backConfirmationMode = rememberIntSettingState(settings.backConfirmationMode)
    val showPostLinkPreviewMode = rememberBooleanSettingState(settings.showPostLinkPreviews)
    val postActionbarMode = rememberIntSettingState(settings.postActionbarMode)

    val snackbarHostState = remember { SnackbarHostState() }

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
                postNavigationGestureMode = postNavigationGestureModeState.value,
            ),
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            SimpleTopAppBar(text = "Theme", onClickBack = onBack)
        },
        content = { padding ->
            Column(
                modifier =
                    Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(padding),
            ) {
                SettingsCheckbox(
                    state = secureWindowState,
                    title = {
                        Text(text = stringResource(R.string.look_and_feel_secure_window))
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
            }
        },
    )
}
