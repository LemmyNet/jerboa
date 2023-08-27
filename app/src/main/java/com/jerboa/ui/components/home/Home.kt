package com.jerboa.ui.components.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationCity
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Sort
import androidx.compose.material.icons.outlined.ViewAgenda
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.jerboa.datatypes.types.ListingType
import com.jerboa.datatypes.types.SortType
import com.jerboa.datatypes.types.Tagline
import com.jerboa.getLocalizedListingTypeName
import com.jerboa.ui.components.common.MenuItem
import com.jerboa.ui.components.common.MyMarkdownText
import com.jerboa.ui.components.common.SortOptionsDialog
import com.jerboa.ui.components.common.SortTopOptionsDialog
import com.jerboa.ui.theme.LARGE_PADDING
import com.jerboa.util.cascade.CascadeDropdownMenu
import kotlinx.collections.immutable.ImmutableList

@Composable
fun HomeHeaderTitle(
    selectedSortType: SortType,
    selectedListingType: ListingType,
) {
    val ctx = LocalContext.current
    Column {
        Text(
            text = getLocalizedListingTypeName(ctx, selectedListingType),
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = ctx.getString(selectedSortType.shortForm),
            style = MaterialTheme.typography.titleSmall,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeHeader(
    openDrawer: () -> Unit,
    onClickSortType: (SortType) -> Unit,
    onClickListingType: (ListingType) -> Unit,
    onClickRefresh: () -> Unit,
    onClickPostViewMode: (PostViewMode) -> Unit,
    selectedSortType: SortType,
    selectedListingType: ListingType,
    selectedPostViewMode: PostViewMode,
    onClickSiteInfo: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    siteVersion: String,
) {
    var showSortOptions by remember { mutableStateOf(false) }
    var showTopOptions by remember { mutableStateOf(false) }
    var showListingTypeOptions by remember { mutableStateOf(false) }
    var showMoreOptions by remember { mutableStateOf(false) }

    if (showSortOptions) {
        SortOptionsDialog(
            selectedSortType = selectedSortType,
            onDismissRequest = { showSortOptions = false },
            onClickSortType = {
                showSortOptions = false
                onClickSortType(it)
            },
            onClickSortTopOptions = {
                showSortOptions = false
                showTopOptions = !showTopOptions
            },
            siteVersion = siteVersion,
        )
    }

    if (showTopOptions) {
        SortTopOptionsDialog(
            selectedSortType = selectedSortType,
            onDismissRequest = { showTopOptions = false },
            onClickSortType = {
                showTopOptions = false
                onClickSortType(it)
            },
            siteVersion = siteVersion,
        )
    }

    TopAppBar(
        scrollBehavior = scrollBehavior,
        title = {
            HomeHeaderTitle(
                selectedSortType = selectedSortType,
                selectedListingType = selectedListingType,
            )
        },
        navigationIcon = {
            IconButton(onClick = openDrawer) {
                Icon(
                    Icons.Outlined.Menu,
                    contentDescription = stringResource(R.string.home_menu),
                )
            }
        },
        // No Idea why, but the tint for this is muted?
        actions = {
            Box {
                IconButton(onClick = {
                    showListingTypeOptions = !showListingTypeOptions
                }) {
                    Icon(
                        Icons.Outlined.FilterList,
                        contentDescription = stringResource(R.string.homeHeader_filter),
                    )
                }

                ListingTypeOptionsDropDown(
                    expanded = showListingTypeOptions,
                    onDismissRequest = { showListingTypeOptions = false },
                    onClickListingType = {
                        showListingTypeOptions = false
                        onClickListingType(it)
                    },
                    selectedListingType = selectedListingType,
                )
            }
            IconButton(modifier = Modifier.testTag("jerboa:sortoptions"), onClick = {
                showSortOptions = !showSortOptions
            }) {
                Icon(
                    Icons.Outlined.Sort,
                    contentDescription = stringResource(R.string.selectSort),
                )
            }
            Box {
                IconButton(
                    modifier = Modifier.testTag("jerboa:options"),
                    onClick = { showMoreOptions = !showMoreOptions },
                ) {
                    Icon(
                        Icons.Outlined.MoreVert,
                        contentDescription = stringResource(R.string.moreOptions),
                    )
                }
                HomeMoreDropdown(
                    expanded = showMoreOptions,
                    onDismissRequest = { showMoreOptions = false },
                    onClickRefresh = onClickRefresh,
                    onClickSiteInfo = onClickSiteInfo,
                    selectedPostViewMode = selectedPostViewMode,
                    onClickPostViewMode = onClickPostViewMode,
                )
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun HomeHeaderPreview() {
    HomeHeader(
        openDrawer = {},
        onClickSortType = {},
        onClickListingType = {},
        onClickRefresh = {},
        onClickPostViewMode = {},
        onClickSiteInfo = {},
        selectedSortType = SortType.Hot,
        selectedListingType = ListingType.All,
        selectedPostViewMode = PostViewMode.Card,
        scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
        siteVersion = MINIMUM_API_VERSION,
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HomeMoreDropdown(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onClickSiteInfo: () -> Unit,
    onClickRefresh: () -> Unit,
    onClickPostViewMode: (PostViewMode) -> Unit,
    selectedPostViewMode: PostViewMode,
) {
    CascadeDropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = Modifier.semantics { testTagsAsResourceId = true },
    ) {
        DropdownMenuItem(
            text = { Text(text = stringResource(R.string.home_refresh)) },
            leadingIcon = { Icon(Icons.Outlined.Refresh, contentDescription = null) },
            onClick = {
                onDismissRequest()
                onClickRefresh()
            },
            modifier = Modifier.testTag("jerboa:refresh"),
        )
        DropdownMenuItem(
            text = { Text(text = stringResource(R.string.home_post_view_mode)) },
            leadingIcon = { Icon(Icons.Outlined.ViewAgenda, contentDescription = null) },
            children = {
                PostViewMode.entries.map {
                    DropdownMenuItem(
                        text = { Text(text = stringResource(it.mode)) },
                        onClick = {
                            onDismissRequest()
                            onClickPostViewMode(it)
                        },
                        modifier = if (selectedPostViewMode == it) {
                            Modifier.background(MaterialTheme.colorScheme.onBackground.copy(alpha = .1f))
                        } else {
                            Modifier
                        }.testTag("jerboa:postviewmode_${it.name}"),
                    )
                }
            },
            modifier = Modifier.testTag("jerboa:postviewmode"),
        )
        DropdownMenuItem(
            text = { Text(stringResource(R.string.home_site_info)) },
            leadingIcon = { Icon(Icons.Outlined.Info, contentDescription = null) },
            onClick = {
                onDismissRequest()
                onClickSiteInfo()
            },
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ListingTypeOptionsDropDown(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onClickListingType: (ListingType) -> Unit,
    selectedListingType: ListingType,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = Modifier.semantics { testTagsAsResourceId = true },
    ) {
        MenuItem(
            text = stringResource(R.string.dialogs_subscribed),
            icon = Icons.Outlined.Bookmarks,
            onClick = { onClickListingType(ListingType.Subscribed) },
            highlight = (selectedListingType == ListingType.Subscribed),
        )
        // TODO hide local for non-federated instances
        MenuItem(
            text = stringResource(R.string.dialogs_local),
            icon = Icons.Outlined.LocationCity,
            onClick = { onClickListingType(ListingType.Local) },
            highlight = (selectedListingType == ListingType.Local),
        )
        MenuItem(
            text = stringResource(R.string.dialogs_all),
            icon = Icons.Outlined.Public,
            onClick = { onClickListingType(ListingType.All) },
            highlight = (selectedListingType == ListingType.All),
        )
    }
}

@Composable
fun Taglines(taglines: ImmutableList<Tagline>) {
    if (taglines.isNotEmpty()) {
        val tagline by remember { mutableStateOf(taglines.random()) }
        Column(
            Modifier.padding(LARGE_PADDING),
        ) {
            MyMarkdownText(
                markdown = tagline.content,
                onClick = {},
            )
        }
    }
}
