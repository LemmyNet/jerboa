package com.jerboa.ui.components.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jerboa.datatypes.*
import com.jerboa.datatypes.api.GetUnreadCountResponse
import com.jerboa.datatypes.api.MyUserInfo
import com.jerboa.db.Account
import com.jerboa.ui.components.common.*
import com.jerboa.ui.components.community.CommunityLinkLarger
import com.jerboa.ui.components.person.PersonName
import com.jerboa.ui.theme.*
import com.jerboa.unreadCountTotal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun Drawer(
    navController: NavController = rememberNavController(),
    accounts: List<Account>? = null,
    onSwitchAccountClick: (account: Account) -> Unit,
    onSignOutClick: () -> Unit,
    onClickListingType: (ListingType) -> Unit,
    myUserInfo: MyUserInfo?,
    onCommunityClick: (community: CommunitySafe) -> Unit,
    onClickProfile: () -> Unit,
    onClickInbox: () -> Unit,
    onClickSaved: () -> Unit,
    onClickSettings: () -> Unit,
    unreadCounts: GetUnreadCountResponse?
) {
    var showAccountAddMode by rememberSaveable { mutableStateOf(false) }

    DrawerHeader(
        myPerson = myUserInfo?.local_user_view?.person,
        showAccountAddMode = showAccountAddMode,
        onClickShowAccountAddMode = { showAccountAddMode = !showAccountAddMode }
    )
    Divider()
    // Drawer items
    DrawerContent(
        accounts = accounts,
        unreadCounts = unreadCounts,
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
        onClickSettings = onClickSettings
    )
}

@Composable
fun DrawerContent(
    showAccountAddMode: Boolean,
    navController: NavController,
    accounts: List<Account>?,
    onSwitchAccountClick: (account: Account) -> Unit,
    onSignOutClick: () -> Unit,
    onClickListingType: (ListingType) -> Unit,
    onCommunityClick: (community: CommunitySafe) -> Unit,
    onClickProfile: () -> Unit,
    onClickInbox: () -> Unit,
    onClickSaved: () -> Unit,
    onClickSettings: () -> Unit,
    myUserInfo: MyUserInfo?,
    unreadCounts: GetUnreadCountResponse?
) {
    AnimatedVisibility(
        visible = showAccountAddMode,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        DrawerAddAccountMode(
            accounts = accounts,
            navController = navController,
            onSwitchAccountClick = onSwitchAccountClick,
            onSignOutClick = onSignOutClick
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
            unreadCounts = unreadCounts,
            onClickSettings = onClickSettings
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
    onClickListingType: (ListingType) -> Unit,
    onCommunityClick: (community: CommunitySafe) -> Unit,
    unreadCounts: GetUnreadCountResponse? = null
) {
    val listState = rememberLazyListState()

    val totalUnreads = unreadCounts?.let { unreadCountTotal(it) }
    val follows = myUserInfo?.follows

    LazyColumn(
        state = listState,
        modifier = Modifier.simpleVerticalScrollbar(listState)
    ) {
        if (!follows.isNullOrEmpty()) {
            item {
                IconAndTextDrawerItem(
                    text = "Subscribed",
                    icon = Icons.Default.Bookmarks,
                    onClick = { onClickListingType(ListingType.Subscribed) }
                )
            }
        }
        item {
            IconAndTextDrawerItem(
                text = "Local",
                icon = Icons.Default.LocationCity,
                onClick = { onClickListingType(ListingType.Local) }
            )
        }
        item {
            IconAndTextDrawerItem(
                text = "All",
                icon = Icons.Default.Public,
                onClick = { onClickListingType(ListingType.All) }
            )
        }
        item {
            myUserInfo?.also {
                IconAndTextDrawerItem(
                    text = "Saved",
                    icon = Icons.Default.Bookmarks,
                    onClick = onClickSaved
                )
            }
        }
        item {
            myUserInfo?.also {
                Divider()
            }
        }
        item {
            myUserInfo?.also {
                IconAndTextDrawerItem(
                    text = "Profile",
                    icon = Icons.Default.Person,
                    onClick = onClickProfile
                )
            }
        }
        item {
            myUserInfo?.also {
                IconAndTextDrawerItem(
                    text = "Inbox",
                    icon = Icons.Default.Email,
                    onClick = onClickInbox,
                    iconBadgeCount = totalUnreads
                )
            }
        }
        item {
            myUserInfo?.also {
                IconAndTextDrawerItem(
                    text = "Settings",
                    icon = Icons.Default.Settings,
                    onClick = onClickSettings
                )
            }
        }
        item {
            myUserInfo?.also {
                Divider()
            }
        }

        follows?.also { follows ->
            item {
                Text(
                    text = "Subscriptions",
                    modifier = Modifier.padding(LARGE_PADDING),
                    style = MaterialTheme.typography.subtitle1,
                    color = MaterialTheme.colors.onBackground.muted
                )
            }
            items(
                follows,
                key = { follow -> follow.community.id }
            ) { follow ->
                CommunityLinkLarger(
                    community = follow.community,
                    onClick = onCommunityClick
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
        onClickSettings = {}
    )
}

@Composable
fun DrawerAddAccountMode(
    navController: NavController = rememberNavController(),
    accounts: List<Account>? = null,
    onSwitchAccountClick: (account: Account) -> Unit,
    onSignOutClick: () -> Unit
) {
    val accountsWithoutCurrent = accounts?.toMutableList()
    val currentAccount = getCurrentAccount(accounts)
    accountsWithoutCurrent?.remove(currentAccount)

    Column {
        IconAndTextDrawerItem(
            text = "Add Account",
            icon = Icons.Default.Add,
            onClick = { navController.navigate(route = "login") }
        )
        accountsWithoutCurrent?.forEach {
            IconAndTextDrawerItem(
                text = "Switch to ${it.instance}/${it.name}",
                icon = Icons.Default.Login,
                onClick = { onSwitchAccountClick(it) }
            )
        }
        accounts?.also {
            IconAndTextDrawerItem(
                text = "Sign Out",
                icon = Icons.Default.Close,
                onClick = onSignOutClick
            )
        }
    }
}

@Preview
@Composable
fun DrawerAddAccountModePreview() {
    DrawerAddAccountMode(
        onSignOutClick = {},
        onSwitchAccountClick = {}
    )
}

@Composable
fun DrawerHeader(
    myPerson: PersonSafe?,
    onClickShowAccountAddMode: () -> Unit,
    showAccountAddMode: Boolean = false
) {
    val sizeMod = Modifier
        .fillMaxWidth()
        .height(DRAWER_BANNER_SIZE)

    Box(
        modifier = sizeMod
            .clickable(onClick = onClickShowAccountAddMode)
    ) {
        myPerson?.banner?.also {
            PictrsBannerImage(
                url = it
            )
        }
        // banner
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = sizeMod
                .padding(XL_PADDING)
        ) {
            AvatarAndAccountName(myPerson)
            Icon(
                imageVector = if (showAccountAddMode) {
                    Icons.Default.ExpandLess
                } else {
                    Icons.Default.ExpandMore
                },
                contentDescription = "TODO"
            )
        }
    }
}

@Composable
fun AvatarAndAccountName(myPerson: PersonSafe?) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SMALL_PADDING)
    ) {
        myPerson?.avatar?.also {
            LargerCircularIcon(icon = it)
        }
        PersonName(
            person = myPerson,
            color = Color.White
        )
    }
}

@Preview
@Composable
fun DrawerHeaderPreview() {
    DrawerHeader(
        myPerson = samplePersonSafe,
        onClickShowAccountAddMode = {}
    )
}

@Composable
fun IconAndTextDrawerItem(
    text: String,
    icon: ImageVector? = null,
    iconBadgeCount: Int? = null,
    onClick: () -> Unit,
    more: Boolean = false,
    highlight: Boolean = false
) {
    val spacingMod = Modifier
        .padding(LARGE_PADDING)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(
                color = if (highlight) {
                    colorShade(
                        MaterialTheme.colors.background,
                        2f
                    )
                } else {
                    Color.Transparent
                }
            )
    ) {
        Row {
            icon?.also { ico ->
                InboxIconAndBadge(
                    iconBadgeCount = iconBadgeCount,
                    modifier = spacingMod.size(DRAWER_ITEM_SPACING),
                    icon = ico,
                    tint = MaterialTheme.colors.onSurface
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.subtitle1,
                modifier = spacingMod
            )
        }
        if (more) {
            Icon(
                imageVector = Icons.Default.ArrowRight,
                contentDescription = "TODO",
                tint = MaterialTheme.colors.onSurface,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Preview
@Composable
fun IconAndTextDrawerItemPreview() {
    IconAndTextDrawerItem(
        text = "A test item",
        onClick = {}
    )
}

@Preview
@Composable
fun IconAndTextDrawerItemWithMorePreview() {
    IconAndTextDrawerItem(
        text = "A test item",
        onClick = {},
        more = true
    )
}

@Composable
fun HomeHeaderTitle(
    selectedSortType: SortType,
    selectedListingType: ListingType
) {
    Column {
        Text(
            text = selectedListingType.toString(),
            style = MaterialTheme.typography.subtitle1
        )
        Text(
            text = selectedSortType.toString(),
            style = MaterialTheme.typography.body1,
            color = contentColorFor(MaterialTheme.colors.primarySurface).muted
        )
    }
}

@Composable
fun HomeHeader(
    scope: CoroutineScope,
    scaffoldState: ScaffoldState,
    onClickSortType: (SortType) -> Unit,
    onClickListingType: (ListingType) -> Unit,
    onClickRefresh: () -> Unit,
    selectedSortType: SortType,
    selectedListingType: ListingType,
    navController: NavController
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
            }
        )
    }

    if (showTopOptions) {
        SortTopOptionsDialog(
            selectedSortType = selectedSortType,
            onDismissRequest = { showTopOptions = false },
            onClickSortType = {
                showTopOptions = false
                onClickSortType(it)
            }
        )
    }

    if (showListingTypeOptions) {
        ListingTypeOptionsDialog(
            selectedListingType = selectedListingType,
            onDismissRequest = { showListingTypeOptions = false },
            onClickListingType = {
                showListingTypeOptions = false
                onClickListingType(it)
            }
        )
    }

    if (showMoreOptions) {
        HomeMoreDialog(
            onDismissRequest = { showMoreOptions = false },
            onClickRefresh = onClickRefresh,
            navController = navController
        )
    }

    val backgroundColor = MaterialTheme.colors.primarySurface
    val contentColor = contentColorFor(backgroundColor)

    TopAppBar(
        title = {
            HomeHeaderTitle(
                selectedSortType = selectedSortType,
                selectedListingType = selectedListingType
            )
        },
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        elevation = APP_BAR_ELEVATION,
        navigationIcon = {
            IconButton(onClick = {
                scope.launch {
                    scaffoldState.drawerState.open()
                }
            }) {
                Icon(
                    Icons.Filled.Menu,
                    contentDescription = "Menu"
                )
            }
        },
        // No Idea why, but the tint for this is muted?
        actions = {
            IconButton(onClick = {
                showListingTypeOptions = !showListingTypeOptions
            }) {
                Icon(
                    Icons.Default.FilterList,
                    contentDescription = "TODO",
                    tint = contentColor
                )
            }
            IconButton(onClick = {
                showSortOptions = !showSortOptions
            }) {
                Icon(
                    Icons.Default.Sort,
                    contentDescription = "TODO",
                    tint = contentColor
                )
            }
            IconButton(onClick = {
                showMoreOptions = !showMoreOptions
            }) {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = "TODO",
                    tint = contentColor
                )
            }
        }
    )
}

@Preview
@Composable
fun HomeHeaderPreview() {
    val scope = rememberCoroutineScope()
    val scaffoldState =
        rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
    HomeHeader(
        scope,
        scaffoldState,
        selectedSortType = SortType.Hot,
        selectedListingType = ListingType.All,
        navController = rememberNavController(),
        onClickListingType = {},
        onClickSortType = {},
        onClickRefresh = {}
    )
}

@Composable
fun HomeMoreDialog(
    onDismissRequest: () -> Unit,
    navController: NavController,
    onClickRefresh: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        text = {
            Column {
                IconAndTextDrawerItem(
                    text = "Refresh",
                    icon = Icons.Default.Refresh,
                    onClick = {
                        onDismissRequest()
                        onClickRefresh()
                    }
                )
                IconAndTextDrawerItem(
                    text = "View Sidebar",
                    icon = Icons.Default.Info,
                    onClick = {
                        navController.navigate("siteSidebar")
                        onDismissRequest()
                    }
                )
            }
        },
        buttons = {}
    )
}
