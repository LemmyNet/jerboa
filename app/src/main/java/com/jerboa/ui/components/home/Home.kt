package com.jerboa.ui.components.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jerboa.colorShade
import com.jerboa.datatypes.ListingType
import com.jerboa.datatypes.PersonSafe
import com.jerboa.datatypes.SortType
import com.jerboa.datatypes.samplePersonSafe
import com.jerboa.db.Account
import com.jerboa.getCurrentAccount
import com.jerboa.ui.components.common.LargerCircularIcon
import com.jerboa.ui.components.common.PictrsBannerImage
import com.jerboa.ui.components.person.PersonName
import com.jerboa.ui.theme.DRAWER_BANNER_SIZE
import com.jerboa.ui.theme.Muted
import com.jerboa.ui.theme.SMALL_PADDING
import com.jerboa.ui.theme.XL_PADDING
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun Drawer(
    navController: NavController = rememberNavController(),
    accounts: List<Account>? = null,
    onSwitchAccountClick: (account: Account) -> Unit = {},
    onSignOutClick: () -> Unit = {},
    myPerson: PersonSafe?,
) {
    var showAccountAddMode by rememberSaveable { mutableStateOf(false) }

    DrawerHeader(
        myPerson = myPerson,
        showAccountAddMode = showAccountAddMode,
        clickShowAccountAddMode = { showAccountAddMode = !showAccountAddMode }
    )
    Divider()
    // Drawer items
    DrawerContent(
        accounts = accounts,
        showAccountAddMode = showAccountAddMode,
        navController = navController,
        onSwitchAccountClick = onSwitchAccountClick,
        onSignOutClick = onSignOutClick,
    )
}

@Composable
fun DrawerContent(
    showAccountAddMode: Boolean,
    navController: NavController,
    accounts: List<Account>?,
    onSwitchAccountClick: (account: Account) -> Unit,
    onSignOutClick: () -> Unit,
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
        DrawerItemsMain()
    }
}

@Composable
fun DrawerItemsMain(
    onClickSubscribed: () -> Unit = {},
    onClickLocal: () -> Unit = {},
    onClickAll: () -> Unit = {},
    onClickSaved: () -> Unit = {},
    onClickProfile: () -> Unit = {},
) {
    LazyColumn {
        item {
            IconAndTextDrawerItem(
                text = "Subscribed",
                icon = Icons.Default.Bookmarks,
                onClick = onClickSubscribed,
            )
        }
        item {
            IconAndTextDrawerItem(
                text = "Local",
                icon = Icons.Default.LocationCity,
                onClick = onClickLocal,
            )
        }
        item {
            IconAndTextDrawerItem(
                text = "All",
                icon = Icons.Default.Public,
                onClick = onClickAll,
            )
        }
        item {
            IconAndTextDrawerItem(
                text = "Saved",
                icon = Icons.Default.Star,
                onClick = onClickSaved,
            )
        }
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
    val ctx = LocalContext.current

    val accountsWithoutCurrent = accounts?.toMutableList()
    accounts?.also { accounts ->
        val currentAccount = getCurrentAccount(accounts)
        accountsWithoutCurrent?.remove(currentAccount)
    }

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
            myPerson?.also {
                AvatarAndAccountName(myPerson)
            }
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
fun AvatarAndAccountName(myPerson: PersonSafe) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SMALL_PADDING)
    ) {
        myPerson.avatar?.also {
            LargerCircularIcon(icon = it)
        }
        PersonName(person = myPerson, color = MaterialTheme.colors.onSurface)
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
    onClick: () -> Unit,
    more: Boolean = false,
    highlight: Boolean = false,
) {

    val spacingMod = Modifier
        .padding(12.dp)

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
            icon?.also {
                Icon(
                    imageVector = icon,
                    contentDescription = "TODO",
                    tint = MaterialTheme.colors.onSurface,
                    modifier = spacingMod.size(24.dp)

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
) {

    var showSortOptions by remember { mutableStateOf(false) }
    var showTopOptions by remember { mutableStateOf(false) }
    var showListingTypeOptions by remember { mutableStateOf(false) }

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

    TopAppBar(
        title = {
            HomeHeaderTitle(
                selectedSortType = selectedSortType,
                selectedListingType = selectedListingType
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

@Composable
fun SortOptionsDialog(
    onDismissRequest: () -> Unit = {},
    onClickSortType: (SortType) -> Unit = {},
    onClickSortTopOptions: () -> Unit = {},
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

val topSortTypes = listOf(
    SortType.TopDay,
    SortType.TopWeek,
    SortType.TopMonth,
    SortType.TopYear,
    SortType.TopAll,
)

@Composable
fun SortTopOptionsDialog(
    onDismissRequest: () -> Unit = {},
    onClickSortType: (SortType) -> Unit = {},
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
    SortOptionsDialog(selectedSortType = SortType.Hot)
}

@Composable
fun ListingTypeOptionsDialog(
    onDismissRequest: () -> Unit = {},
    onClickListingType: (ListingType) -> Unit = {},
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

@Preview
@Composable
fun ListingTypeOptionsDialogPreview() {
    ListingTypeOptionsDialog(selectedListingType = ListingType.Local)
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
        selectedListingType = ListingType.All
    )
}
