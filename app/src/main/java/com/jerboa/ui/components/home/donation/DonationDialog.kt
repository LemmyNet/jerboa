package com.jerboa.ui.components.home.donation

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jerboa.R
import com.jerboa.api.ApiState
import com.jerboa.model.LocalUserViewModel
import com.jerboa.model.SiteViewModel
import com.jerboa.ui.components.common.MarkdownHelper
import com.jerboa.ui.components.common.MyMarkdownText
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

const val DONATION_LINK = "https://join-lemmy.org/donate"
val DONATION_MARKDOWN =
    """
    ### Support Lemmy Development
    
    We are able to develop [Lemmy](https://github.com/LemmyNet/lemmy) as an **open source** platform free of tracking and
    ads thanks to the generosity of our users.
    
    Once a year we ask you to consider donating to support our work.
    Financial security allows us to continue maintaining and improving the platform.
    If you’d like to make a one-time or recurring donation simply click the button below.
    
    **Thank you for using Lemmy.**
    
    Nutomic and Dessalines,
    
    Lemmy Developers

    """.trimIndent()

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ShowDonationNotification(siteViewModel: SiteViewModel) {
    var showDonationDialog by rememberSaveable { mutableStateOf(false) }
    val localUserViewModel: LocalUserViewModel = viewModel()

    LaunchedEffect(siteViewModel.siteRes) {
        when (val siteRes = siteViewModel.siteRes) {
            is ApiState.Success -> {
                val lastDonationNotification = siteRes.data.my_user
                    ?.local_user_view
                    ?.local_user
                    ?.last_donation_notification
                    ?: return@LaunchedEffect
                if (lastDonationNotification.isEmpty()) return@LaunchedEffect

                try {
                    val lastDonationTime = OffsetDateTime.parse(lastDonationNotification, DateTimeFormatter.ISO_DATE_TIME)
                    val oneYearAgo = OffsetDateTime.now().minusYears(1)
                    showDonationDialog = lastDonationTime.isBefore(oneYearAgo)
                } catch (e: Exception) {
                    Log.e("ShowDonationNotification", "could not parse datetime", e)
                }
            }
            else -> {}
        }
    }

    if (showDonationDialog) {
        val uriHandler = LocalUriHandler.current
        DonationNotificationDialog(
            onClick = {
                uriHandler.openUri(DONATION_LINK)
                localUserViewModel.markDonationNotificationShown {
                    showDonationDialog = false
                }
            },
            onDismiss = {
                localUserViewModel.markDonationNotificationShown {
                    showDonationDialog = false
                }
            },
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DonationNotificationDialog(
    onClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        text = {
            Column {
                MyMarkdownText(
                    markdown = DONATION_MARKDOWN,
                    onClick = {},
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onClick,
            ) {
                Text(stringResource(R.string.donate))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
            ) {
                Text(stringResource(R.string.donation_dialog_dismiss))
            }
        },
        onDismissRequest = onDismiss,
        modifier = Modifier.semantics { testTagsAsResourceId = true },
    )
}

@Preview
@Composable
fun DonationNotificationDialogPreview() {
    MarkdownHelper.init(LocalContext.current)
    DonationNotificationDialog({}, {})
}
