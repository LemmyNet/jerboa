package com.jerboa.ui.components.common

import android.util.Log
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.jerboa.UnreadOrAll
import com.jerboa.datatypes.ListingType
import com.jerboa.datatypes.SortType
import com.jerboa.db.AppSettingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request

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
    SortType.TopAll
)

@Composable
fun SortTopOptionsDialog(
    onDismissRequest: () -> Unit,
    onClickSortType: (SortType) -> Unit,
    selectedSortType: SortType
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        text = {
            Column {
                IconAndTextDrawerItem(
                    text = "Top Day",
                    onClick = { onClickSortType(SortType.TopDay) },
                    highlight = (selectedSortType == SortType.TopDay)
                )
                IconAndTextDrawerItem(
                    text = "Top Week",
                    onClick = { onClickSortType(SortType.TopWeek) },
                    highlight = (selectedSortType == SortType.TopWeek)
                )
                IconAndTextDrawerItem(
                    text = "Top Month",
                    onClick = { onClickSortType(SortType.TopMonth) },
                    highlight = (selectedSortType == SortType.TopMonth)
                )
                IconAndTextDrawerItem(
                    text = "Top Year",
                    onClick = { onClickSortType(SortType.TopYear) },
                    highlight = (selectedSortType == SortType.TopYear)
                )
                IconAndTextDrawerItem(
                    text = "Top All Time",
                    onClick = { onClickSortType(SortType.TopAll) },
                    highlight = (selectedSortType == SortType.TopAll)
                )
            }
        },
        confirmButton = {}
    )
}

@Preview
@Composable
fun SortOptionsDialogPreview() {
    SortOptionsDialog(
        selectedSortType = SortType.Hot,
        onDismissRequest = {},
        onClickSortTopOptions = {},
        onClickSortType = {}
    )
}

@Composable
fun ListingTypeOptionsDialog(
    onDismissRequest: () -> Unit,
    onClickListingType: (ListingType) -> Unit,
    selectedListingType: ListingType
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        text = {
            Column {
                IconAndTextDrawerItem(
                    text = "Subscribed",
                    icon = Icons.Outlined.Bookmarks,
                    onClick = { onClickListingType(ListingType.Subscribed) },
                    highlight = (selectedListingType == ListingType.Subscribed)
                )
                // TODO hide local for non-federated instances
                IconAndTextDrawerItem(
                    text = "Local",
                    icon = Icons.Outlined.LocationCity,
                    onClick = { onClickListingType(ListingType.Local) },
                    highlight = (selectedListingType == ListingType.Local)
                )
                IconAndTextDrawerItem(
                    text = "All",
                    icon = Icons.Outlined.Public,
                    onClick = { onClickListingType(ListingType.All) },
                    highlight = (selectedListingType == ListingType.All)
                )
            }
        },
        confirmButton = {}
    )
}

@Composable
fun SortOptionsDialog(
    onDismissRequest: () -> Unit,
    onClickSortType: (SortType) -> Unit,
    onClickSortTopOptions: () -> Unit,
    selectedSortType: SortType
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        text = {
            Column {
                IconAndTextDrawerItem(
                    text = "Active",
                    icon = Icons.Outlined.Moving,
                    onClick = { onClickSortType(SortType.Active) },
                    highlight = (selectedSortType == SortType.Active)
                )
                IconAndTextDrawerItem(
                    text = "Hot",
                    icon = Icons.Outlined.LocalFireDepartment,
                    onClick = { onClickSortType(SortType.Hot) },
                    highlight = (selectedSortType == SortType.Hot)
                )
                IconAndTextDrawerItem(
                    text = "New",
                    icon = Icons.Outlined.BrightnessLow,
                    onClick = { onClickSortType(SortType.New) },
                    highlight = (selectedSortType == SortType.New)
                )
                IconAndTextDrawerItem(
                    text = "Most Comments",
                    icon = Icons.Outlined.FormatListNumbered,
                    onClick = { onClickSortType(SortType.MostComments) },
                    highlight = (selectedSortType == SortType.MostComments)
                )
                IconAndTextDrawerItem(
                    text = "New Comments",
                    icon = Icons.Outlined.NewReleases,
                    onClick = { onClickSortType(SortType.NewComments) },
                    highlight = (selectedSortType == SortType.NewComments)
                )
                IconAndTextDrawerItem(
                    text = "Top",
                    icon = Icons.Outlined.BarChart,
                    onClick = onClickSortTopOptions,
                    more = true,
                    highlight = (topSortTypes.contains(selectedSortType))
                )
            }
        },
        confirmButton = {}
    )
}

@Composable
fun UnreadOrAllOptionsDialog(
    onDismissRequest: () -> Unit,
    onClickUnreadOrAll: (UnreadOrAll) -> Unit,
    selectedUnreadOrAll: UnreadOrAll
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        text = {
            Column {
                IconAndTextDrawerItem(
                    text = "All",
                    icon = Icons.Outlined.List,
                    onClick = { onClickUnreadOrAll(UnreadOrAll.All) },
                    highlight = (selectedUnreadOrAll == UnreadOrAll.All)
                )
                // TODO hide local for non-federated instances
                IconAndTextDrawerItem(
                    text = "Unread",
                    icon = Icons.Outlined.MarkunreadMailbox,
                    onClick = { onClickUnreadOrAll(UnreadOrAll.Unread) },
                    highlight = (selectedUnreadOrAll == UnreadOrAll.Unread)
                )
            }
        },
        confirmButton = {}
    )
}

@Preview
@Composable
fun ListingTypeOptionsDialogPreview() {
    ListingTypeOptionsDialog(
        selectedListingType = ListingType.Local,
        onClickListingType = {},
        onDismissRequest = {}
    )
}

@Composable
fun ShowChangelog(appSettingsViewModel: AppSettingsViewModel) {
    val changelogViewed = appSettingsViewModel.appSettings.observeAsState().value?.viewedChangelog

    // Make sure its initialized
    changelogViewed?.also { cViewed ->
        val viewed = cViewed == 1

        val whatsChangedDialogOpen = remember { mutableStateOf(!viewed) }

        if (whatsChangedDialogOpen.value) {
            val scrollState = rememberScrollState()
            val scope = rememberCoroutineScope()
            val markdown = remember { mutableStateOf("") }

            AlertDialog(
                text = {
                    Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState)) {
                        MyMarkdownText(markdown = DONATION_MARKDOWN + markdown.value)
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            whatsChangedDialogOpen.value = false
                            appSettingsViewModel.markChangelogViewed()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Done")
                    }
                },
                onDismissRequest = {
                    whatsChangedDialogOpen.value = false
                    appSettingsViewModel.markChangelogViewed()
                }
            )

            scope.launch(Dispatchers.IO) {
                Log.d("jerboa", "Fetching RELEASES.md ...")
                // Fetch the markdown text
                val client = OkHttpClient()
                val releasesUrl = "https://raw.githubusercontent.com/dessalines/jerboa/main/RELEASES.md".toHttpUrl()
                val req = Request.Builder().url(releasesUrl).build()
                val res = client.newCall(req).execute()
                markdown.value = res.body?.string() ?: ""
            }
        }
    }
}
