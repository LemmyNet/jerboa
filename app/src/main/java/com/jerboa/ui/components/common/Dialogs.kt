package com.jerboa.ui.components.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.BrightnessLow
import androidx.compose.material.icons.outlined.FormatListNumbered
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.LocationCity
import androidx.compose.material.icons.outlined.MarkunreadMailbox
import androidx.compose.material.icons.outlined.Moving
import androidx.compose.material.icons.outlined.NewReleases
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.jerboa.PostViewMode
import com.jerboa.R
import com.jerboa.UnreadOrAll
import com.jerboa.datatypes.types.CommentSortType
import com.jerboa.datatypes.types.ListingType
import com.jerboa.datatypes.types.SortType
import com.jerboa.db.AppSettingsViewModel

val DONATION_MARKDOWN = """
    ### Support Jerboa
    
    Jerboa is free, open-source software, meaning no advertising, monetizing, or venture capital, 
    ever. Your donations directly support full-time development of the project.

    - [Support on Liberapay](https://liberapay.com/Lemmy).
    - [Support on Patreon](https://www.patreon.com/dessalines).
    - [Support on OpenCollective](https://opencollective.com/lemmy).

""".trimIndent()

val topSortTypes = listOf(
    SortType.TopDay,
    SortType.TopWeek,
    SortType.TopMonth,
    SortType.TopYear,
    SortType.TopAll,
)

@Composable
fun SortTopOptionsDialog(
    onDismissRequest: () -> Unit,
    onClickSortType: (SortType) -> Unit,
    selectedSortType: SortType,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        text = {
            Column {
                IconAndTextDrawerItem(
                    text = stringResource(R.string.dialogs_top_day),
                    onClick = { onClickSortType(SortType.TopDay) },
                    highlight = (selectedSortType == SortType.TopDay),
                )
                IconAndTextDrawerItem(
                    text = stringResource(R.string.dialogs_top_week),
                    onClick = { onClickSortType(SortType.TopWeek) },
                    highlight = (selectedSortType == SortType.TopWeek),
                )
                IconAndTextDrawerItem(
                    text = stringResource(R.string.dialogs_top_month),
                    onClick = { onClickSortType(SortType.TopMonth) },
                    highlight = (selectedSortType == SortType.TopMonth),
                )
                IconAndTextDrawerItem(
                    text = stringResource(R.string.dialogs_top_year),
                    onClick = { onClickSortType(SortType.TopYear) },
                    highlight = (selectedSortType == SortType.TopYear),
                )
                IconAndTextDrawerItem(
                    text = stringResource(R.string.dialogs_top_all_time),
                    onClick = { onClickSortType(SortType.TopAll) },
                    highlight = (selectedSortType == SortType.TopAll),
                )
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
    )
}

@Composable
fun ListingTypeOptionsDialog(
    onDismissRequest: () -> Unit,
    onClickListingType: (ListingType) -> Unit,
    selectedListingType: ListingType,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        text = {
            Column {
                IconAndTextDrawerItem(
                    text = stringResource(R.string.dialogs_subscribed),
                    icon = Icons.Outlined.Bookmarks,
                    onClick = { onClickListingType(ListingType.Subscribed) },
                    highlight = (selectedListingType == ListingType.Subscribed),
                )
                // TODO hide local for non-federated instances
                IconAndTextDrawerItem(
                    text = stringResource(R.string.dialogs_local),
                    icon = Icons.Outlined.LocationCity,
                    onClick = { onClickListingType(ListingType.Local) },
                    highlight = (selectedListingType == ListingType.Local),
                )
                IconAndTextDrawerItem(
                    text = stringResource(R.string.dialogs_all),
                    icon = Icons.Outlined.Public,
                    onClick = { onClickListingType(ListingType.All) },
                    highlight = (selectedListingType == ListingType.All),
                )
            }
        },
        confirmButton = {},
    )
}

@Composable
fun SortOptionsDialog(
    onDismissRequest: () -> Unit,
    onClickSortType: (SortType) -> Unit,
    onClickSortTopOptions: () -> Unit,
    selectedSortType: SortType,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        text = {
            Column {
                IconAndTextDrawerItem(
                    text = stringResource(R.string.dialogs_active),
                    icon = Icons.Outlined.Moving,
                    onClick = { onClickSortType(SortType.Active) },
                    highlight = (selectedSortType == SortType.Active),
                )
                IconAndTextDrawerItem(
                    text = stringResource(R.string.dialogs_hot),
                    icon = Icons.Outlined.LocalFireDepartment,
                    onClick = { onClickSortType(SortType.Hot) },
                    highlight = (selectedSortType == SortType.Hot),
                )
                IconAndTextDrawerItem(
                    text = stringResource(R.string.dialogs_new),
                    icon = Icons.Outlined.BrightnessLow,
                    onClick = { onClickSortType(SortType.New) },
                    highlight = (selectedSortType == SortType.New),
                )
                IconAndTextDrawerItem(
                    text = stringResource(R.string.dialogs_most_comments),
                    icon = Icons.Outlined.FormatListNumbered,
                    onClick = { onClickSortType(SortType.MostComments) },
                    highlight = (selectedSortType == SortType.MostComments),
                )
                IconAndTextDrawerItem(
                    text = stringResource(R.string.dialogs_new_comments),
                    icon = Icons.Outlined.NewReleases,
                    onClick = { onClickSortType(SortType.NewComments) },
                    highlight = (selectedSortType == SortType.NewComments),
                )
                IconAndTextDrawerItem(
                    text = stringResource(R.string.dialogs_top),
                    icon = Icons.Outlined.BarChart,
                    onClick = onClickSortTopOptions,
                    more = true,
                    highlight = (topSortTypes.contains(selectedSortType)),
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
    )
}

@Composable
fun CommentSortOptionsDialog(
    onDismissRequest: () -> Unit,
    onClickSortType: (CommentSortType) -> Unit,
    selectedSortType: CommentSortType,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        text = {
            Column {
                IconAndTextDrawerItem(
                    text = stringResource(R.string.dialogs_hot),
                    icon = Icons.Outlined.LocalFireDepartment,
                    onClick = { onClickSortType(CommentSortType.Hot) },
                    highlight = (selectedSortType == CommentSortType.Hot),
                )
                IconAndTextDrawerItem(
                    text = stringResource(R.string.dialogs_top),
                    icon = Icons.Outlined.BarChart,
                    onClick = { onClickSortType(CommentSortType.Top) },
                    highlight = (selectedSortType == CommentSortType.Top),
                )
                IconAndTextDrawerItem(
                    text = stringResource(R.string.dialogs_new),
                    icon = Icons.Outlined.NewReleases,
                    onClick = { onClickSortType(CommentSortType.New) },
                    highlight = (selectedSortType == CommentSortType.New),
                )
                IconAndTextDrawerItem(
                    text = stringResource(R.string.dialogs_old),
                    icon = Icons.Outlined.History,
                    onClick = { onClickSortType(CommentSortType.Old) },
                    highlight = (selectedSortType == CommentSortType.Old),
                )
            }
        },
        confirmButton = {},
    )
}

@Composable
fun UnreadOrAllOptionsDialog(
    onDismissRequest: () -> Unit,
    onClickUnreadOrAll: (UnreadOrAll) -> Unit,
    selectedUnreadOrAll: UnreadOrAll,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        text = {
            Column {
                IconAndTextDrawerItem(
                    text = stringResource(R.string.dialogs_all),
                    icon = Icons.Outlined.List,
                    onClick = { onClickUnreadOrAll(UnreadOrAll.All) },
                    highlight = (selectedUnreadOrAll == UnreadOrAll.All),
                )
                // TODO hide local for non-federated instances
                IconAndTextDrawerItem(
                    text = stringResource(R.string.dialogs_unread),
                    icon = Icons.Outlined.MarkunreadMailbox,
                    onClick = { onClickUnreadOrAll(UnreadOrAll.Unread) },
                    highlight = (selectedUnreadOrAll == UnreadOrAll.Unread),
                )
            }
        },
        confirmButton = {},
    )
}

@Composable
fun PostViewModeDialog(
    onDismissRequest: () -> Unit,
    onClickPostViewMode: (PostViewMode) -> Unit,
    selectedPostViewMode: PostViewMode,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        text = {
            Column {
                PostViewMode.values().map {
                    IconAndTextDrawerItem(
                        text = stringResource(it.mode),
                        onClick = { onClickPostViewMode(it) },
                        highlight = (selectedPostViewMode == it),
                    )
                }
            }
        },
        confirmButton = {},
    )
}

@Preview
@Composable
fun ListingTypeOptionsDialogPreview() {
    ListingTypeOptionsDialog(
        selectedListingType = ListingType.Local,
        onClickListingType = {},
        onDismissRequest = {},
    )
}

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
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(stringResource(R.string.dialogs_done))
                    }
                },
                onDismissRequest = {
                    whatsChangedDialogOpen = false
                    appSettingsViewModel.markChangelogViewed()
                },
            )
        }
    }
}
