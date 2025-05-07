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
import com.jerboa.DONATE_LINK
import com.jerboa.api.API
import com.jerboa.api.ApiState
import com.jerboa.getVersionCode
import com.jerboa.model.AppSettingsViewModel
import com.jerboa.model.SiteViewModel
import com.jerboa.ui.components.common.DonationNotificationDialog
import com.jerboa.ui.components.common.ShowChangelog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.OffsetDateTime

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ShowAppStartupDialogs(
    appSettingsViewModel: AppSettingsViewModel,
    siteViewModel: SiteViewModel,
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    var activeDialog by rememberSaveable { mutableStateOf<DialogType?>(null) }

    val appSettings = appSettingsViewModel.appSettings.observeAsState().value
    val currentVersionCode = ctx.getVersionCode()

    val lastViewedVersion = appSettings?.lastVersionCodeViewed

    // Check which dialogs need to be shown
    val changelogNeedsToShow = shouldShowChangelog(lastViewedVersion, currentVersionCode)
    val donationNeedsToShow = shouldShowDonation(siteViewModel)

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

        null -> {} // No dialog
    }
}

fun shouldShowChangelog(
    lastViewedVersion: Int?,
    currentVersionCode: Int,
): Boolean = lastViewedVersion != null && lastViewedVersion != currentVersionCode

fun shouldShowDonation(siteViewModel: SiteViewModel): Boolean {
    val siteRes = siteViewModel.siteRes
    if (siteRes !is ApiState.Success) return false

    val lastDonationNotification = siteRes.data.my_user
        ?.local_user_view
        ?.local_user
        ?.last_donation_notification
        ?: return false

    return try {
        val lastDonationTime = OffsetDateTime.parse(lastDonationNotification)
        val oneYearAgo = OffsetDateTime.now().minusYears(1)
        lastDonationTime.isBefore(oneYearAgo)
    } catch (e: Exception) {
        Log.e("ShowAppStartupDialogs", "Failed to parse donation time", e)
        false
    }
}

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
