package com.jerboa.ui.components.common

import androidx.compose.foundation.layout.Column
import androidx.compose.material.AlertDialog
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.jerboa.UnreadOrAll
import com.jerboa.datatypes.ListingType
import com.jerboa.datatypes.SortType
import com.jerboa.ui.components.home.IconAndTextDrawerItem

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
                    text = "Top Day",
                    onClick = { onClickSortType(SortType.TopDay) },
                    highlight = (selectedSortType == SortType.TopDay),
                )
                IconAndTextDrawerItem(
                    text = "Top Week",
                    onClick = { onClickSortType(SortType.TopWeek) },
                    highlight = (selectedSortType == SortType.TopWeek),
                )
                IconAndTextDrawerItem(
                    text = "Top Month",
                    onClick = { onClickSortType(SortType.TopMonth) },
                    highlight = (selectedSortType == SortType.TopMonth),
                )
                IconAndTextDrawerItem(
                    text = "Top Year",
                    onClick = { onClickSortType(SortType.TopYear) },
                    highlight = (selectedSortType == SortType.TopYear),
                )
                IconAndTextDrawerItem(
                    text = "Top All Time",
                    onClick = { onClickSortType(SortType.TopAll) },
                    highlight = (selectedSortType == SortType.TopAll),
                )
            }
        },
        buttons = {},
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
                    text = "Subscribed",
                    icon = Icons.Default.Bookmarks,
                    onClick = { onClickListingType(ListingType.Subscribed) },
                    highlight = (selectedListingType == ListingType.Subscribed),
                )
                // TODO hide local for non-federated instances
                IconAndTextDrawerItem(
                    text = "Local",
                    icon = Icons.Default.LocationCity,
                    onClick = { onClickListingType(ListingType.Local) },
                    highlight = (selectedListingType == ListingType.Local),
                )
                IconAndTextDrawerItem(
                    text = "All",
                    icon = Icons.Default.Public,
                    onClick = { onClickListingType(ListingType.All) },
                    highlight = (selectedListingType == ListingType.All),
                )
            }
        },
        buttons = {},
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
                    text = "Active",
                    icon = Icons.Default.Moving,
                    onClick = { onClickSortType(SortType.Active) },
                    highlight = (selectedSortType == SortType.Active),
                )
                IconAndTextDrawerItem(
                    text = "Hot",
                    icon = Icons.Default.LocalFireDepartment,
                    onClick = { onClickSortType(SortType.Hot) },
                    highlight = (selectedSortType == SortType.Hot),
                )
                IconAndTextDrawerItem(
                    text = "New",
                    icon = Icons.Default.BrightnessLow,
                    onClick = { onClickSortType(SortType.New) },
                    highlight = (selectedSortType == SortType.New),
                )
                IconAndTextDrawerItem(
                    text = "Most Comments",
                    icon = Icons.Default.FormatListNumbered,
                    onClick = { onClickSortType(SortType.MostComments) },
                    highlight = (selectedSortType == SortType.MostComments),
                )
                IconAndTextDrawerItem(
                    text = "New Comments",
                    icon = Icons.Default.NewReleases,
                    onClick = { onClickSortType(SortType.NewComments) },
                    highlight = (selectedSortType == SortType.NewComments),
                )
                IconAndTextDrawerItem(
                    text = "Top",
                    icon = Icons.Default.BarChart,
                    onClick = onClickSortTopOptions,
                    more = true,
                    highlight = (topSortTypes.contains(selectedSortType)),
                )
            }
        },
        buttons = {},
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
                    text = "All",
                    icon = Icons.Default.List,
                    onClick = { onClickUnreadOrAll(UnreadOrAll.All) },
                    highlight = (selectedUnreadOrAll == UnreadOrAll.All),
                )
                // TODO hide local for non-federated instances
                IconAndTextDrawerItem(
                    text = "Unread",
                    icon = Icons.Default.MarkunreadMailbox,
                    onClick = { onClickUnreadOrAll(UnreadOrAll.Unread) },
                    highlight = (selectedUnreadOrAll == UnreadOrAll.Unread),
                )
            }
        },
        buttons = {},
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
