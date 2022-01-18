package com.jerboa.ui.components.home

import androidx.compose.animation.AnimatedVisibility
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
import com.jerboa.*
import com.jerboa.datatypes.*
import com.jerboa.datatypes.api.GetUnreadCountResponse
import com.jerboa.datatypes.api.MyUserInfo
import com.jerboa.db.Account
import com.jerboa.ui.components.common.LargerCircularIcon
import com.jerboa.ui.components.common.PictrsBannerImage
import com.jerboa.ui.components.community.CommunityLinkLarger
import com.jerboa.ui.components.person.PersonName
import com.jerboa.ui.theme.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun Drawer(
    navController: NavController = rememberNavController(),
    accounts: List<Account>? = null,
    onSwitchAccountClick: (account: Account) -> Unit = {},
    onSignOutClick: () -> Unit = {},
    onClickListingType: (ListingType) -> Unit = {},
    myUserInfo: MyUserInfo?,
    onCommunityClick: (community: CommunitySafe) -> Unit = {},
    onClickProfile: () -> Unit = {},
    onClickInbox: () -> Unit = {},
    unreadCounts: GetUnreadCountResponse?,
) {
    var showAccountAddMode by rememberSaveable { mutableStateOf(false) }

    DrawerHeader(
        myPerson = myUserInfo?.local_user_view?.person,
        showAccountAddMode = showAccountAddMode,
        clickShowAccountAddMode = { showAccountAddMode = !showAccountAddMode }
    )
    Divider()
    // Drawer items
    DrawerContent(
        accounts = accounts,
        unreadCounts = unreadCounts,
        follows = myUserInfo?.follows,
        showAccountAddMode = showAccountAddMode,
        navController = navController,
        onSwitchAccountClick = onSwitchAccountClick,
        onSignOutClick = onSignOutClick,
        onClickListingType = onClickListingType,
        onCommunityClick = onCommunityClick,
        onClickProfile = onClickProfile,
        onClickInbox = onClickInbox,
    )
}

@Composable
fun DrawerContent(
    showAccountAddMode: Boolean,
    navController: NavController,
    accounts: List<Account>?,
    onSwitchAccountClick: (account: Account) -> Unit,
    onSignOutClick: () -> Unit,
    onClickListingType: (ListingType) -> Unit = {},
    onCommunityClick: (community: CommunitySafe) -> Unit = {},
    onClickProfile: () -> Unit = {},
    onClickInbox: () -> Unit = {},
    follows: List<CommunityFollowerView>?,
    unreadCounts: GetUnreadCountResponse?,
) {
    AnimatedVisibility(
        visible = showAccountAddMode,
    ) {
        DrawerAddAccountMode(
            accounts = accounts,
            navController = navController,
            onSwitchAccountClick = onSwitchAccountClick,
            onSignOutClick = onSignOutClick
        )
    }

    AnimatedVisibility(
        visible = !showAccountAddMode,
    ) {
        DrawerItemsMain(
            onClickListingType = onClickListingType,
            onCommunityClick = onCommunityClick,
            onClickProfile = onClickProfile,
            onClickInbox = onClickInbox,
            follows = follows,
            unreadCounts = unreadCounts,
        )
    }
}

@Composable
fun DrawerItemsMain(
    follows: List<CommunityFollowerView>? = null,
    onClickSaved: () -> Unit = {},
    onClickProfile: () -> Unit = {},
    onClickInbox: () -> Unit = {},
    onClickListingType: (ListingType) -> Unit = {},
    onCommunityClick: (community: CommunitySafe) -> Unit = {},
    unreadCounts: GetUnreadCountResponse? = null,
) {
    val listState = rememberLazyListState()

    val totalUnreads = unreadCounts?.let { unreadCountTotal(it) }

    LazyColumn(
        state = listState,
    ) {
        item {
            IconAndTextDrawerItem(
                text = "Subscribed",
                icon = Icons.Default.Bookmarks,
                onClick = { onClickListingType(ListingType.Subscribed) },
            )
        }
        item {
            IconAndTextDrawerItem(
                text = "Local",
                icon = Icons.Default.LocationCity,
                onClick = { onClickListingType(ListingType.Local) },
            )
        }
        item {
            IconAndTextDrawerItem(
                text = "All",
                icon = Icons.Default.Public,
                onClick = { onClickListingType(ListingType.All) },
            )
        }
        // TODO add saved
//        item {
//            IconAndTextDrawerItem(
//                text = "Saved",
//                icon = Icons.Default.Star,
//                onClick = onClickSaved,
//            )
//        }
        item {
            Divider()
        }
        item {
            IconAndTextDrawerItem(
                text = "Profile",
                icon = Icons.Default.Person,
                onClick = onClickProfile,
            )
        }
        item {
            IconAndTextDrawerItem(
                text = "Inbox",
                icon = Icons.Default.Email,
                onClick = onClickInbox,
                iconBadgeCount = totalUnreads,
            )
        }
        item {
            Divider()
        }

        follows?.also { follows ->
            item {
                Text(
                    text = "Subscriptions",
                    modifier = Modifier.padding(LARGE_PADDING),
                    style = MaterialTheme.typography.subtitle1,
                    color = Muted,
                )
            }
            items(follows) { follow ->
                CommunityLinkLarger(
                    community = follow.community,
                    onClick = onCommunityClick,
                )
            }
        }
    }
}

@Preview
@Composable
fun DrawerItemsMainPreview() {
    DrawerItemsMain()
}

@Composable
fun DrawerAddAccountMode(
    navController: NavController = rememberNavController(),
    accounts: List<Account>? = null,
    onSwitchAccountClick: (account: Account) -> Unit = {},
    onSignOutClick: () -> Unit = {},
) {
    val accountsWithoutCurrent = accounts?.toMutableList()
    val currentAccount = getCurrentAccount(accounts)
    accountsWithoutCurrent?.remove(currentAccount)

    Column {
        IconAndTextDrawerItem(
            text = "Add Account",
            icon = Icons.Default.Add,
            onClick = { navController.navigate(route = "login") },
        )
        accountsWithoutCurrent?.forEach {
            IconAndTextDrawerItem(
                text = "Switch to ${it.name}",
                icon = Icons.Default.Login,
                onClick = { onSwitchAccountClick(it) },
            )
        }
        accounts?.also {
            IconAndTextDrawerItem(
                text = "Sign Out",
                icon = Icons.Default.Close,
                onClick = onSignOutClick,
            )
        }
    }
}

@Preview
@Composable
fun DrawerAddAccountModePreview() {
    DrawerAddAccountMode()
}

@Composable
fun DrawerHeader(
    myPerson: PersonSafe?,
    clickShowAccountAddMode: () -> Unit = {},
    showAccountAddMode: Boolean = false,
) {
    val sizeMod = Modifier
        .fillMaxWidth()
        .height(DRAWER_BANNER_SIZE)

    Box(
        modifier = sizeMod
            .clickable(onClick = clickShowAccountAddMode),
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
                .padding(XL_PADDING)
        ) {
            AvatarAndAccountName(myPerson)
            Icon(
                imageVector = if (showAccountAddMode) {
                    Icons.Default.ExpandLess
                } else {
                    Icons.Default.ExpandMore
                },
                contentDescription = "TODO",
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
            color = MaterialTheme.colors.onSurface,
        )
    }
}

@Preview
@Composable
fun DrawerHeaderPreview() {
    DrawerHeader(myPerson = samplePersonSafe)
}

@Composable
fun IconAndTextDrawerItem(
    text: String,
    icon: ImageVector? = null,
    iconBadgeCount: Int? = null,
    onClick: () -> Unit,
    more: Boolean = false,
    highlight: Boolean = false,
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
                        MaterialTheme.colors.background, 2f
                    )
                } else {
                    Color
                        .Transparent
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

@Composable
private fun InboxIconAndBadge(
    iconBadgeCount: Int?,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier,
) {
    if (iconBadgeCount !== null && iconBadgeCount > 0) {
        BadgedBox(
            modifier = modifier,
            badge = {
                Badge(
                    content = {
                        Text(
                            text = iconBadgeCount.toString(),
                        )
                    },
                )
            },
            content = {
                Icon(
                    imageVector = icon,
                    contentDescription = "TODO",
                    tint = tint,
                )
            },
        )
    } else {
        Icon(
            imageVector = icon,
            contentDescription = "TODO",
            tint = tint,
            modifier = modifier
        )
    }
}

@Preview
@Composable
fun IconAndTextDrawerItemPreview() {
    IconAndTextDrawerItem(
        text = "A test item",
        onClick = {},
    )
}

@Preview
@Composable
fun IconAndTextDrawerItemWithMorePreview() {
    IconAndTextDrawerItem(
        text = "A test item",
        onClick = {},
        more = true,
    )
}

@Composable
fun HomeHeaderTitle(
    selectedSortType: SortType,
    selectedListingType: ListingType,
) {
    Column {
        Text(
            text = selectedListingType.toString(),
            style = MaterialTheme.typography.subtitle1
        )
        Text(
            text = selectedSortType.toString(),
            style = MaterialTheme.typography.body1,
            color = Muted,
        )
    }
}

@Composable
fun HomeHeader(
    scope: CoroutineScope,
    scaffoldState: ScaffoldState,
    onClickSortType: (SortType) -> Unit = {},
    onClickListingType: (ListingType) -> Unit = {},
    selectedSortType: SortType,
    selectedListingType: ListingType,
    navController: NavController,
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
            navController = navController,
        )
    }

    TopAppBar(
        title = {
            HomeHeaderTitle(
                selectedSortType = selectedSortType,
                selectedListingType = selectedListingType,
            )
        },
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
                    tint = MaterialTheme.colors.onSurface
                )
            }
            IconButton(onClick = {
                showSortOptions = !showSortOptions
            }) {
                Icon(
                    Icons.Default.Sort,
                    contentDescription = "TODO",
                    tint = MaterialTheme.colors.onSurface
                )
            }
            IconButton(onClick = {
                showMoreOptions = !showMoreOptions
            }) {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = "TODO",
                    tint = MaterialTheme.colors.onSurface
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
    )
}

@Composable
fun BottomAppBarAll(
    navController: NavController = rememberNavController(),
    unreadCounts: GetUnreadCountResponse? = null,
    onClickProfile: () -> Unit = {},
    onClickInbox: () -> Unit = {},
) {
    var selectedState by remember { mutableStateOf("home") }
    val totalUnreads = unreadCounts?.let { unreadCountTotal(it) }

    BottomAppBar(
        elevation = 10.dp,
        backgroundColor = MaterialTheme.colors.background
    ) {
        BottomNavigationItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "TODO",
                )
            },
            selectedContentColor = MaterialTheme.colors.primary,
            unselectedContentColor = Muted,
            onClick = {
                selectedState = "home"
                navController.navigate("home")
            },
            selected = selectedState == "home"
        )

        BottomNavigationItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.List,
                    contentDescription = "TODO",
                )
            },
            selectedContentColor = MaterialTheme.colors.primary,
            unselectedContentColor = Muted,
            onClick = {
                selectedState = "communityList"
                navController.navigate("communityList")
            },
            selected = selectedState == "communityList"
        )
        BottomNavigationItem(
            icon = {
                InboxIconAndBadge(
                    iconBadgeCount = totalUnreads,
                    icon = Icons.Default.Email,
                    tint = if (selectedState == "inbox") {
                        MaterialTheme.colors.primary
                    } else {
                        Muted
                    },
                )
            },
            selectedContentColor = MaterialTheme.colors.primary,
            unselectedContentColor = Muted,
            onClick = {
                selectedState = "inbox"
                onClickInbox()
            },
            selected = selectedState == "inbox"
        )
        BottomNavigationItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "TODO",
                )
            },
            selectedContentColor = MaterialTheme.colors.primary,
            unselectedContentColor = Muted,
            onClick = {
                selectedState = "personProfile"
                onClickProfile()
            },
            selected = selectedState == "personProfile"
        )
    }
}

@Preview
@Composable
fun BottomAppBarAllPreview() {
    BottomAppBarAll()
}

@Composable
fun HomeMoreDialog(
    onDismissRequest: () -> Unit = {},
    navController: NavController,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        text = {
            Column {
                IconAndTextDrawerItem(
                    text = "View Sidebar",
                    icon = Icons.Default.Info,
                    onClick = {
                        navController.navigate("sidebar")
                        onDismissRequest()
                    },
                )
            }
        },
        buttons = {},
    )
}
