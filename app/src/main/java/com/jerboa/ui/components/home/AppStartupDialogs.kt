package com.jerboa.ui.components.home

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import com.jerboa.BuildConfig
import com.jerboa.DONATE_LINK
import com.jerboa.api.API
import com.jerboa.api.toOpt
import com.jerboa.getVersionCode
import com.jerboa.model.AppSettingsViewModel
import com.jerboa.model.MyUserInfoViewModel
import com.jerboa.shouldShowDonation
import com.jerboa.ui.components.common.DonationNotificationDialog
import com.jerboa.ui.components.common.ShowChangelog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ShowAppStartupDialogs(
    appSettingsViewModel: AppSettingsViewModel,
    myUserInfoViewModel: MyUserInfoViewModel,
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    var activeDialog by rememberSaveable { mutableStateOf<DialogType?>(null) }

    val appSettings = appSettingsViewModel.appSettings.observeAsState().value
    val currentVersionCode = ctx.getVersionCode()

    val lastViewedVersion = appSettings?.lastVersionCodeViewed

    // Check which dialogs need to be shown
    val changelogNeedsToShow = shouldShowChangelog(lastViewedVersion, currentVersionCode)
    val donationNeedsToShow = myUserInfoViewModel.myUserRes.toOpt().shouldShowDonation()

    // Determine which dialog to show
    LaunchedEffect(changelogNeedsToShow, donationNeedsToShow) {
        // If a dialog is already showing, don't interrupt it
        if (activeDialog != null) return@LaunchedEffect

        if (changelogNeedsToShow) {
            activeDialog = DialogType.Changelog
        } else if (donationNeedsToShow) {
            activeDialog = DialogType.Donation
        }
    }

    when (activeDialog) {
        DialogType.Changelog -> {
            val markdown = appSettingsViewModel.changelog
            LaunchedEffect(appSettingsViewModel) {
                appSettingsViewModel.loadChangelog(ctx)
            }

            ShowChangelog(
                onClick = {
                    appSettingsViewModel.updateLastVersionCodeViewed(currentVersionCode)
                    activeDialog = null
                },
                onDismiss = {
                    appSettingsViewModel.updateLastVersionCodeViewed(currentVersionCode)
                    activeDialog = null
                },
                changelogMarkdown = markdown,
            )
        }

        DialogType.Donation -> {
            // Only include donation text in f-droid
            if (BuildConfig.FLAVOR == "fdroid") {
                val uriHandler = LocalUriHandler.current
                DonationNotificationDialog(
                    onClick = {
                        uriHandler.openUri(DONATE_LINK)
                        markDonationNotificationShown(scope = scope) {
                            activeDialog = null
                        }
                    },
                    onDismiss = {
                        markDonationNotificationShown(scope = scope) {
                            activeDialog = null
                        }
                    },
                )
            }
        }

        null -> {} // No dialog
    }
}

fun shouldShowChangelog(
    lastViewedVersion: Int?,
    currentVersionCode: Int,
): Boolean = lastViewedVersion != null && lastViewedVersion != currentVersionCode

fun markDonationNotificationShown(
    scope: CoroutineScope,
    onComplete: () -> Unit,
) {
    val api = API.getInstanceOrNull()

    if (api?.FF?.markDonationDialogShown() != true) {
        onComplete()
        return
    }

    scope.launch {
        api
            .markDonationDialogShown()
            .onSuccess { onComplete() }
            .onFailure {
                Log.e("markDonationNotificationShown", "Failed to mark donation shown", it)
                onComplete()
            }
    }
}

enum class DialogType {
    Changelog,
    Donation,
}
