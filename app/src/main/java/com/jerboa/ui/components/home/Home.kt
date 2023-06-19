package com.jerboa.ui.components.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.LocationCity
import androidx.compose.material.icons.outlined.Login
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Sort
import androidx.compose.material.icons.outlined.ViewAgenda
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jerboa.PostViewMode
import com.jerboa.R
import com.jerboa.api.ApiState
import com.jerboa.datatypes.samplePerson
import com.jerboa.datatypes.types.Community
import com.jerboa.datatypes.types.GetSiteResponse
import com.jerboa.datatypes.types.ListingType
import com.jerboa.datatypes.types.MyUserInfo
import com.jerboa.datatypes.types.Person
import com.jerboa.datatypes.types.SortType
import com.jerboa.datatypes.types.Tagline
import com.jerboa.db.Account
import com.jerboa.db.AccountViewModel
import com.jerboa.getLocalizedListingTypeName
import com.jerboa.getLocalizedSortingTypeName
import com.jerboa.ui.components.common.IconAndTextDrawerItem
import com.jerboa.ui.components.common.LargerCircularIcon
import com.jerboa.ui.components.common.ListingTypeOptionsDialog
import com.jerboa.ui.components.common.MyMarkdownText
import com.jerboa.ui.components.common.PictrsBannerImage
import com.jerboa.ui.components.common.PostViewModeDialog
import com.jerboa.ui.components.common.SortOptionsDialog
import com.jerboa.ui.components.common.SortTopOptionsDialog
import com.jerboa.ui.components.common.simpleVerticalScrollbar
import com.jerboa.ui.components.community.CommunityLinkLarger
import com.jerboa.ui.components.person.PersonName
import com.jerboa.ui.theme.DRAWER_BANNER_SIZE
import com.jerboa.ui.theme.LARGE_PADDING
import com.jerboa.ui.theme.SMALL_PADDING
import com.jerboa.ui.theme.XL_PADDING
import com.jerboa.ui.theme.muted
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun Drawer(
    siteRes: ApiState<GetSiteResponse>,
    unreadCount: Int,
    navController: NavController = rememberNavController(),
    accountViewModel: AccountViewModel,
    onSwitchAccountClick: (account: Account) -> Unit,
    onSignOutClick: () -> Unit,
    onClickListingType: (ListingType) -> Unit,
    onCommunityClick: (community: Community) -> Unit,
    onClickProfile: () -> Unit,
    onClickInbox: () -> Unit,
    onClickSaved: () -> Unit,
    onClickSettings: () -> Unit,
    onClickCommunities: () -> Unit,
    isOpen: Boolean,
) {
    var showAccountAddMode by rememberSaveable { mutableStateOf(false) }

    if (!isOpen) showAccountAddMode = false

    val myUserInfo = when (siteRes) {
        is ApiState.Success -> siteRes.data.my_user
        else -> null
    }

    DrawerHeader(
        myPerson = myUserInfo?.local_user_view?.person,
        showAccountAddMode = showAccountAddMode,
        onClickShowAccountAddMode = { showAccountAddMode = !showAccountAddMode },
        showAvatar = myUserInfo?.local_user_view?.local_user?.show_avatars ?: true,
    )
    Divider()
    // Drawer items
    DrawerContent(
        accountViewModel = accountViewModel,
        unreadCount = unreadCount,
        myUserInfo = myUserInfo,
        showAccountAddMode = showAccountAddMode,
        navController = navController,
        onSwitchAccountClick = onSwitchAccountClick,
        onSignOutClick = onSignOutClick,
        onClickListingType = onClickListingType,
        onCommunityClick = onCommunityClick,
        onClickProfile = onClickProfile,
        onClickInbox = onClickInbox,
        onClickSaved = onClickSaved,
        onClickSettings = onClickSettings,
        onClickCommunities = onClickCommunities,
    )
}

@Composable
fun DrawerContent(
    showAccountAddMode: Boolean,
    navController: NavController,
    accountViewModel: AccountViewModel,
    onSwitchAccountClick: (account: Account) -> Unit,
    onSignOutClick: () -> Unit,
    onClickListingType: (ListingType) -> Unit,
    onCommunityClick: (community: Community) -> Unit,
    onClickProfile: () -> Unit,
    onClickInbox: () -> Unit,
    onClickSaved: () -> Unit,
    onClickSettings: () -> Unit,
    onClickCommunities: () -> Unit,
    myUserInfo: MyUserInfo?,
    unreadCount: Int,
) {
    AnimatedVisibility(
        visible = showAccountAddMode,
        enter = expandVertically(),
        exit = shrinkVertically(),
    ) {
        DrawerAddAccountMode(
            accountViewModel = accountViewModel,
            navController = navController,
            onSwitchAccountClick = onSwitchAccountClick,
            onSignOutClick = onSignOutClick,
        )
    }

    if (!showAccountAddMode) {
        DrawerItemsMain(
            myUserInfo = myUserInfo,
            onClickListingType = onClickListingType,
            onCommunityClick = onCommunityClick,
            onClickProfile = onClickProfile,
            onClickInbox = onClickInbox,
            onClickSaved = onClickSaved,
            unreadCount = unreadCount,
            onClickSettings = onClickSettings,
            onClickCommunities = onClickCommunities,
        )
    }
}

@Composable
fun DrawerItemsMain(
    myUserInfo: MyUserInfo?,
    onClickSaved: () -> Unit,
    onClickProfile: () -> Unit,
    onClickInbox: () -> Unit,
    onClickSettings: () -> Unit,
    onClickCommunities: () -> Unit,
    onClickListingType: (ListingType) -> Unit,
    onCommunityClick: (community: Community) -> Unit,
    unreadCount: Int,
) {
    val listState = rememberLazyListState()

    val follows = myUserInfo?.follows

    LazyColumn(
        state = listState,
        modifier = Modifier.simpleVerticalScrollbar(listState),
    ) {
        if (!follows.isNullOrEmpty()) {
            item {
                IconAndTextDrawerItem(
                    text = stringResource(R.string.home_subscribed),
                    icon = Icons.Outlined.Bookmarks,
                    onClick = { onClickListingType(ListingType.Subscribed) },
                )
            }
        }
        item {
            IconAndTextDrawerItem(
                text = stringResource(R.string.home_local),
                icon = Icons.Outlined.LocationCity,
                onClick = { onClickListingType(ListingType.Local) },
            )
        }
        item {
            IconAndTextDrawerItem(
                text = stringResource(R.string.home_all),
                icon = Icons.Outlined.Public,
                onClick = { onClickListingType(ListingType.All) },
            )
        }
        item {
            myUserInfo?.also {
                IconAndTextDrawerItem(
                    text = stringResource(R.string.home_saved),
                    icon = Icons.Outlined.Bookmarks,
                    onClick = onClickSaved,
                )
            }
        }
        item {
            IconAndTextDrawerItem(
                text = stringResource(R.string.home_communities),
                icon = Icons.Outlined.List,
                onClick = onClickCommunities,
            )
        }
        item {
            Divider()
        }
        item {
            myUserInfo?.also {
                IconAndTextDrawerItem(
                    text = stringResource(R.string.home_profile),
                    icon = Icons.Outlined.Person,
                    onClick = onClickProfile,
                )
            }
        }
        item {
            myUserInfo?.also {
                IconAndTextDrawerItem(
                    text = stringResource(R.string.home_inbox),
                    icon = Icons.Outlined.Email,
                    onClick = onClickInbox,
                    iconBadgeCount = unreadCount,
                )
            }
        }
        item {
            IconAndTextDrawerItem(
                text = stringResource(R.string.home_settings),
                icon = Icons.Outlined.Settings,
                onClick = onClickSettings,
            )
        }
        item {
            myUserInfo?.also {
                Divider()
            }
        }

        follows?.also { follows ->
            item {
                Text(
                    text = stringResource(R.string.home_subscriptions),
                    modifier = Modifier.padding(LARGE_PADDING),
                    color = MaterialTheme.colorScheme.onBackground.muted,
                )
            }
            items(
                follows,
                key = { follow -> follow.community.id },
            ) { follow ->
                CommunityLinkLarger(
                    community = follow.community,
                    onClick = onCommunityClick,
                    showDefaultIcon = true,
                )
            }
        }
    }
}

@Preview
@Composable
fun DrawerItemsMainPreview() {
    DrawerItemsMain(
        myUserInfo = null,
        onClickListingType = {},
        onClickProfile = {},
        onClickInbox = {},
        onCommunityClick = {},
        onClickSaved = {},
        onClickSettings = {},
        onClickCommunities = {},
        unreadCount = 2,
    )
}

@Composable
fun DrawerAddAccountMode(
    navController: NavController = rememberNavController(),
    accountViewModel: AccountViewModel?,
    onSwitchAccountClick: (account: Account) -> Unit,
    onSignOutClick: () -> Unit,
) {
    val accountsWithoutCurrent = accountViewModel?.allAccounts?.value?.toMutableList()
    val currentAccount = accountsWithoutCurrent?.firstOrNull { it.current }
    accountsWithoutCurrent?.remove(currentAccount)

    Column {
        IconAndTextDrawerItem(
            text = stringResource(R.string.home_add_account),
            icon = Icons.Outlined.Add,
            onClick = { navController.navigate(route = "login") },
        )
        accountsWithoutCurrent?.forEach {
            IconAndTextDrawerItem(
                text = stringResource(R.string.home_switch_to, it.instance, it.name),
                icon = Icons.Outlined.Login,
                onClick = { onSwitchAccountClick(it) },
            )
        }
        currentAccount?.also {
            IconAndTextDrawerItem(
                text = stringResource(R.string.home_sign_out),
                icon = Icons.Outlined.Close,
                onClick = onSignOutClick,
            )
        }
    }
}

@Preview
@Composable
fun DrawerAddAccountModePreview() {
    DrawerAddAccountMode(
        onSignOutClick = {},
        onSwitchAccountClick = {},
        accountViewModel = null,
    )
}

@Composable
fun DrawerHeader(
    myPerson: Person?,
    onClickShowAccountAddMode: () -> Unit,
    showAccountAddMode: Boolean = false,
    showAvatar: Boolean,
) {
    val sizeMod = Modifier
        .fillMaxWidth()
        .height(DRAWER_BANNER_SIZE)

    Box(
        modifier = sizeMod
            .clickable(onClick = onClickShowAccountAddMode),
    ) {
        myPerson?.banner?.also {
            PictrsBannerImage(
                url = it,
            )
        }
        // banner
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = sizeMod
                .padding(XL_PADDING),
        ) {
            AvatarAndAccountName(myPerson, showAvatar)
            Icon(
                imageVector = if (showAccountAddMode) {
                    Icons.Outlined.ExpandLess
                } else {
                    Icons.Outlined.ExpandMore
                },
                contentDescription = if (showAccountAddMode) {
                    stringResource(R.string.moreOptions)
                } else {
                    stringResource(R.string.lessOptions)
                },
            )
        }
    }
}

@Composable
fun AvatarAndAccountName(myPerson: Person?, showAvatar: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SMALL_PADDING),
    ) {
        if (showAvatar) {
            myPerson?.avatar?.also {
                LargerCircularIcon(icon = it)
            }
        }
        PersonName(
            person = myPerson,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Preview
@Composable
fun DrawerHeaderPreview() {
    DrawerHeader(
        myPerson = samplePerson,
        onClickShowAccountAddMode = {},
        showAvatar = true,
    )
}

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
            text = getLocalizedSortingTypeName(ctx, selectedSortType),
            style = MaterialTheme.typography.titleSmall,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeHeader(
    scope: CoroutineScope,
    drawerState: DrawerState,
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
            IconButton(onClick = {
                scope.launch {
                    drawerState.open()
                }
            }) {
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
            IconButton(onClick = {
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
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    HomeHeader(
        scope,
        drawerState,
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

@Composable
fun HomeMoreDialog(
    onDismissRequest: () -> Unit,
    navController: NavController,
    onClickRefresh: () -> Unit,
    onClickShowPostViewModeDialog: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        text = {
            Column {
                IconAndTextDrawerItem(
                    text = stringResource(R.string.home_refresh),
                    icon = Icons.Outlined.Refresh,
                    onClick = {
                        onDismissRequest()
                        onClickRefresh()
                    },
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
                        navController.navigate("siteSidebar")
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
