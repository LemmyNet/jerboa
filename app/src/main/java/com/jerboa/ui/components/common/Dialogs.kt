package com.jerboa.ui.components.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
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
                    markdown = changelogMarkdown,
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

@Preview
@Composable
fun ChangelogDialogPreview() {
    MarkdownHelper.init(LocalContext.current)
    ShowChangelog({}, {}, "")
}
