package com.jerboa.ui.components.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Sort
import androidx.compose.material.icons.outlined.ViewAgenda
import androidx.compose.material3.AlertDialog
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jerboa.PostViewMode
import com.jerboa.R
import com.jerboa.datatypes.types.ListingType
import com.jerboa.datatypes.types.SortType
import com.jerboa.datatypes.types.Tagline
import com.jerboa.getLocalizedListingTypeName
import com.jerboa.getLocalizedSortingTypeShortName
import com.jerboa.ui.components.common.IconAndTextDrawerItem
import com.jerboa.ui.components.common.ListingTypeOptionsDialog
import com.jerboa.ui.components.common.MyMarkdownText
import com.jerboa.ui.components.common.PostViewModeDialog
import com.jerboa.ui.components.common.SortOptionsDialog
import com.jerboa.ui.components.common.SortTopOptionsDialog
import com.jerboa.ui.components.common.toSiteSideBar
import com.jerboa.ui.theme.LARGE_PADDING

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
            text = getLocalizedSortingTypeShortName(ctx, selectedSortType),
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
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    var showSortOptions by remember { mutableStateOf(false) }
    var showTopOptions by remember { mutableStateOf(false) }
    var showListingTypeOptions by remember { mutableStateOf(false) }
    var showMoreOptions by remember { mutableStateOf(false) }
    var showPostViewModeOptions by remember { mutableStateOf(false) }

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
        )
    }

    if (showListingTypeOptions) {
        ListingTypeOptionsDialog(
            selectedListingType = selectedListingType,
            onDismissRequest = { showListingTypeOptions = false },
            onClickListingType = {
                showListingTypeOptions = false
                onClickListingType(it)
            },
        )
    }

    if (showMoreOptions) {
        HomeMoreDialog(
            onDismissRequest = { showMoreOptions = false },
            onClickRefresh = onClickRefresh,
            onClickShowPostViewModeDialog = {
                showMoreOptions = false
                showPostViewModeOptions = !showPostViewModeOptions
            },
            navController = navController,
        )
    }

    if (showPostViewModeOptions) {
        PostViewModeDialog(
            onDismissRequest = { showPostViewModeOptions = false },
            selectedPostViewMode = selectedPostViewMode,
            onClickPostViewMode = {
                showPostViewModeOptions = false
                onClickPostViewMode(it)
            },
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
            IconButton(onClick = {
                showListingTypeOptions = !showListingTypeOptions
            }) {
                Icon(
                    Icons.Outlined.FilterList,
                    contentDescription = stringResource(R.string.homeHeader_filter),
                )
            }
            IconButton(onClick = {
                showSortOptions = !showSortOptions
            }) {
                Icon(
                    Icons.Outlined.Sort,
                    contentDescription = stringResource(R.string.selectSort),
                )
            }
            IconButton(modifier = Modifier.testTag("jerboa:options"), onClick = {
                showMoreOptions = !showMoreOptions
            }) {
                Icon(
                    Icons.Outlined.MoreVert,
                    contentDescription = stringResource(R.string.moreOptions),
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
        selectedSortType = SortType.Hot,
        selectedListingType = ListingType.All,
        selectedPostViewMode = PostViewMode.Card,
        navController = rememberNavController(),
        scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HomeMoreDialog(
    onDismissRequest: () -> Unit,
    navController: NavController,
    onClickRefresh: () -> Unit,
    onClickShowPostViewModeDialog: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = Modifier.semantics { testTagsAsResourceId = true },
        text = {
            Column {
                IconAndTextDrawerItem(
                    text = stringResource(R.string.home_refresh),
                    icon = Icons.Outlined.Refresh,
                    onClick = {
                        onDismissRequest()
                        onClickRefresh()
                    },
                    modifier = Modifier.testTag("jerboa:refresh"),
                )
                IconAndTextDrawerItem(
                    text = stringResource(R.string.home_post_view_mode),
                    icon = Icons.Outlined.ViewAgenda,
                    onClick = {
                        onDismissRequest()
                        onClickShowPostViewModeDialog()
                    },
                )
                IconAndTextDrawerItem(
                    text = stringResource(R.string.home_site_info),
                    icon = Icons.Outlined.Info,
                    onClick = {
                        navController.toSiteSideBar()
                        onDismissRequest()
                    },
                )
            }
        },
        confirmButton = {},
    )
}

@Composable
fun Taglines(taglines: List<Tagline>) {
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
