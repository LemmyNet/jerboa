package com.jerboa.ui.components.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.tooling.preview.Preview
import com.jerboa.PostViewMode
import com.jerboa.R
import com.jerboa.api.MINIMUM_API_VERSION
import com.jerboa.datatypes.types.CommentSortType
import com.jerboa.datatypes.types.SortType
import com.jerboa.model.AppSettingsViewModel

val DONATION_MARKDOWN = """
    ### Support Jerboa
    
    Jerboa is free, open-source software, meaning no advertising, monetizing, or venture capital, 
    ever. Your donations directly support full-time development of the project.

    - [Support on Liberapay](https://liberapay.com/Lemmy).
    - [Support on Patreon](https://www.patreon.com/dessalines).
    - [Support on OpenCollective](https://opencollective.com/lemmy).

""".trimIndent()

val isTopSort = { sort: SortType -> sort.name.startsWith("Top") }

@Composable
fun SortTopOptionsDialog(
    onDismissRequest: () -> Unit,
    onClickSortType: (SortType) -> Unit,
    selectedSortType: SortType,
    siteVersion: String,
) {
    val ctx = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismissRequest,
        text = {
            Column {
                SortType.getSupportedSortTypes(siteVersion).filter(isTopSort).forEach {
                    IconAndTextDrawerItem(
                        text = ctx.getString(it.longForm),
                        onClick = { onClickSortType(it) },
                        highlight = (selectedSortType == it),
                    )
                }
            }
        },
        confirmButton = {},
    )
}

@Preview
@Composable
fun SortOptionsDialogPreview() {
    SortOptionsDialog(
        selectedSortType = SortType.Hot,
        onDismissRequest = {},
        onClickSortTopOptions = {},
        onClickSortType = {},
        siteVersion = MINIMUM_API_VERSION,
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SortOptionsDialog(
    onDismissRequest: () -> Unit,
    onClickSortType: (SortType) -> Unit,
    onClickSortTopOptions: () -> Unit,
    selectedSortType: SortType,
    siteVersion: String,
) {
    AlertDialog(
        modifier = Modifier.semantics { testTagsAsResourceId = true },
        onDismissRequest = onDismissRequest,
        text = {
            Column {
                SortType.getSupportedSortTypes(siteVersion).filter { !isTopSort(it) }.forEach {
                    IconAndTextDrawerItem(
                        text = stringResource(it.longForm),
                        icon = it.icon,
                        onClick = { onClickSortType(it) },
                        highlight = (selectedSortType == it),
                    )
                }
                IconAndTextDrawerItem(
                    text = stringResource(R.string.dialogs_top),
                    icon = Icons.Outlined.BarChart,
                    onClick = onClickSortTopOptions,
                    more = true,
                    highlight = (isTopSort(selectedSortType)),
                )
            }
        },
        confirmButton = {},
    )
}

@Preview
@Composable
fun CommentSortOptionsDialogPreview() {
    CommentSortOptionsDialog(
        selectedSortType = CommentSortType.Hot,
        onDismissRequest = {},
        onClickSortType = {},
        siteVersion = MINIMUM_API_VERSION,
    )
}

@Composable
fun CommentSortOptionsDialog(
    onDismissRequest: () -> Unit,
    onClickSortType: (CommentSortType) -> Unit,
    selectedSortType: CommentSortType,
    siteVersion: String,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        text = {
            Column {
                CommentSortType.getSupportedSortTypes(siteVersion).forEach {
                    IconAndTextDrawerItem(
                        text = stringResource(it.text),
                        icon = it.icon,
                        onClick = { onClickSortType(it) },
                        highlight = (selectedSortType == it),
                    )
                }
            }
        },
        confirmButton = {},
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PostViewModeDialog(
    onDismissRequest: () -> Unit,
    onClickPostViewMode: (PostViewMode) -> Unit,
    selectedPostViewMode: PostViewMode,
) {
    AlertDialog(
        modifier = Modifier.semantics { testTagsAsResourceId = true },
        onDismissRequest = onDismissRequest,
        text = {
            Column {
                PostViewMode.entries.map {
                    IconAndTextDrawerItem(
                        text = stringResource(it.mode),
                        onClick = { onClickPostViewMode(it) },
                        highlight = (selectedPostViewMode == it),
                        modifier = Modifier.testTag("jerboa:postviewmode_${it.name}"),
                    )
                }
            }
        },
        confirmButton = {},
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ShowChangelog(appSettingsViewModel: AppSettingsViewModel) {
    val changelogViewed = appSettingsViewModel.appSettings.observeAsState().value?.viewedChangelog

    // Make sure its initialized
    changelogViewed?.also { cViewed ->
        val viewed = cViewed == 1

        var whatsChangedDialogOpen by remember { mutableStateOf(!viewed) }

        if (whatsChangedDialogOpen) {
            val scrollState = rememberScrollState()
            val markdown by appSettingsViewModel.changelog.collectAsState()
            LaunchedEffect(appSettingsViewModel) {
                appSettingsViewModel.updateChangelog()
            }

            AlertDialog(
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState),
                    ) {
                        MyMarkdownText(
                            markdown = DONATION_MARKDOWN + markdown,
                            onClick = {},
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            whatsChangedDialogOpen = false
                            appSettingsViewModel.markChangelogViewed()
                        },
                        modifier = Modifier.fillMaxWidth().testTag("jerboa:changelogbtn"),
                    ) {
                        Text(stringResource(R.string.dialogs_done))
                    }
                },
                onDismissRequest = {
                    whatsChangedDialogOpen = false
                    appSettingsViewModel.markChangelogViewed()
                },
                modifier = Modifier.semantics { testTagsAsResourceId = true },
            )
        }
    }
}

@Composable
fun ShowOutdatedServerDialog(siteVersion: String, onConfirm: () -> Unit) {
    AlertDialog(
        text = {
            Text(
                stringResource(
                    R.string.dialogs_server_version_outdated,
                    siteVersion,
                    MINIMUM_API_VERSION,
                ),
            )
        },
        onDismissRequest = { },
        confirmButton = {
            Button(
                onClick = onConfirm,
                content = {
                    Text(stringResource(id = R.string.input_fields_ok))
                },
            )
        },
    )
}
