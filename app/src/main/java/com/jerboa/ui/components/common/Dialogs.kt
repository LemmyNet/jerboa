package com.jerboa.ui.components.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.tooling.preview.Preview
import com.jerboa.R

val CHANGELOG_DONATION_MARKDOWN =
    """
    ### Support Jerboa
    
    [Jerboa](https://github.com/LemmyNet/jerboa) is free, open-source software, meaning no advertising, monetizing, or venture capital,
    ever.

    No one likes recurring donations, but they've proven to be the only
    way open-source software like Lemmy can stay alive. If you find
    yourself using Lemmy every day, please consider donating:

    - [Support on Liberapay](https://liberapay.com/Lemmy).
    - [Support on Patreon](https://www.patreon.com/dessalines).
    - [Support on OpenCollective](https://opencollective.com/lemmy).
    
    ---

    """.trimIndent()

val LEMMY_DONATION_MARKDOWN =
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
fun ShowChangelog(
    onClick: () -> Unit,
    onDismiss: () -> Unit,
    changelogMarkdown: String,
) {
    val scrollState = rememberScrollState()

    AlertDialog(
        text = {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState),
            ) {
                MyMarkdownText(
                    markdown = CHANGELOG_DONATION_MARKDOWN + changelogMarkdown,
                    onClick = {},
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth().testTag("jerboa:changelogbtn"),
            ) {
                Text(stringResource(R.string.dialogs_done))
            }
        },
        onDismissRequest = onDismiss,
        modifier = Modifier.semantics { testTagsAsResourceId = true },
    )
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
                    markdown = LEMMY_DONATION_MARKDOWN,
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
fun ChangelogDialogPreview() {
    MarkdownHelper.init(LocalContext.current)
    ShowChangelog({}, {}, "")
}

@Preview
@Composable
fun DonationNotificationDialogPreview() {
    MarkdownHelper.init(LocalContext.current)
    DonationNotificationDialog({}, {})
}
