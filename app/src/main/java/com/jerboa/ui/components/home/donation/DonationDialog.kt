package com.jerboa.ui.components.home.donation

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
fun ShowDonationNotification(changelogShown: Boolean, siteViewModel: SiteViewModel) {
    var showDonationDialog by remember { mutableStateOf(true) }

    // Avoid showing donation notification twice if changelog was shown
    if (changelogShown) return

    when (val siteRes = siteViewModel.siteRes) {
        is ApiState.Success -> {
            val lastDonationNotification = siteRes.data.my_user?.local_user_view?.local_user?.last_donation_notification ?: return
            if (lastDonationNotification.isEmpty()) return

            val localUserViewModel: LocalUserViewModel = viewModel()

            val lastDonationTime = try {
                OffsetDateTime.parse(lastDonationNotification, DateTimeFormatter.ISO_DATE_TIME)
            } catch (e: Exception) {
                Log.d("ShowDonationNotification", "could not parse datetime", e)
                return
            }

            val oneYearAgo = OffsetDateTime.now().minusYears(1)

            // Show dialog if last donation notification was more than a year ago
            if (lastDonationTime.isBefore(oneYearAgo) && showDonationDialog) {

                val uriHandler = LocalUriHandler.current
                val dismissDialog = {
                    localUserViewModel.markDonationNotificationShown {
                        showDonationDialog = false
                    }
                }

                DonationNotificationDialog(
                    onClick = {
                        uriHandler.openUri(DONATION_LINK)
                        dismissDialog()
                    },
                    onDismiss = dismissDialog
                )
            }
        } else -> {}
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DonationNotificationDialog(onClick: () -> Unit, onDismiss: () -> Unit) {
    MarkdownHelper.init(LocalContext.current)

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
            Button (
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
    DonationNotificationDialog({}, {})
}
