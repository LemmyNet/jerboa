package com.jerboa.ui.components.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.jerboa.datatypes.ListingType
import com.jerboa.datatypes.SortType
import com.jerboa.db.Account
import com.jerboa.getCurrentAccount
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun Drawer(
    navController: NavController = rememberNavController(),
    accounts: List<Account>? = null,
    onSwitchAccountClick: (account: Account) -> Unit = {},
    onSignOutClick: () -> Unit = {},
) {
    var showAccountAddMode by rememberSaveable { mutableStateOf(false) }

    DrawerHeader(
        account = getCurrentAccount(accounts),
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
            accounts = accounts, navController = navController,
            onSwitchAccountClick = onSwitchAccountClick, onSignOutClick = onSignOutClick
        )
    }

    AnimatedVisibility(
        visible = !showAccountAddMode,
    ) {
        DrawerItemsStandard()
    }
}

@Composable
fun DrawerItemsStandard() {
    Column {
        Text("Standard mode")
    }
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
            onClick = { navController.navigate(route = "login") },
            icon = Icons.Default.Add,
        )
        accountsWithoutCurrent?.forEach {
            IconAndTextDrawerItem(
                text = "Switch to ${it.name}",
                onClick = { onSwitchAccountClick(it) },
                icon = Icons.Default.Login,
            )
        }
        accounts?.also {
            IconAndTextDrawerItem(
                text = "Sign Out",
                onClick = onSignOutClick,
                icon = Icons.Default.Close,
            )
        }
    }
}

@Preview
@Composable
fun DrawerAddAccountModePreview() {
    DrawerAddAccountMode()
}

@Preview
@Composable
fun DrawerPreview() {
    Drawer()
}

@Composable
fun DrawerHeader(
    account: Account? = null,
    clickShowAccountAddMode: () -> Unit,
    showAccountAddMode: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(Color.Blue)
            .padding(16.dp)
            .clickable(onClick = clickShowAccountAddMode),
        content = {
            account?.also { Text(text = it.name, color = Color.White) }
            Icon(
                imageVector = if (showAccountAddMode) {
                    Icons.Default.ExpandLess
                } else {
                    Icons.Default.ExpandMore
                },
                contentDescription = "TODO",
                modifier = Modifier.align(Alignment.End)
            )
        },
        verticalArrangement = Arrangement.Bottom,
    )
}

@Composable
fun IconAndTextDrawerItem(
    text: String,
    icon: ImageVector? = null,
    onClick: () -> Unit,
    more: Boolean = false,
) {

    val spacingMod = Modifier
        .padding(12.dp)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
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
            Text(text = text, style = MaterialTheme.typography.subtitle1, modifier = spacingMod)
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
    IconAndTextDrawerItem(text = "A test item", onClick = {})
}

@Preview
@Composable
fun IconAndTextDrawerItemWithMorePreview() {
    IconAndTextDrawerItem(text = "A test item", onClick = {}, more = true)
}

@Composable
fun HomeHeader(
    scope: CoroutineScope,
    scaffoldState: ScaffoldState,
    onClickSortType: (SortType) -> Unit = {},
    onClickListingType: (ListingType) -> Unit = {},
) {

    var showSortOptions by remember { mutableStateOf(false) }
    var showTopOptions by remember { mutableStateOf(false) }
    var showListingTypeOptions by remember { mutableStateOf(false) }

    if (showSortOptions) {
        SortOptionsDialog(
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
            onDismissRequest = { showTopOptions = false },
            onClickSortType = {
                showTopOptions = false
                onClickSortType(it)
            }
        )
    }

    if (showListingTypeOptions) {
        ListingTypeOptionsDialog(
            onDismissRequest = { showListingTypeOptions = false },
            onClickListingType = {
                showListingTypeOptions = false
                onClickListingType(it)
            }
        )
    }

    TopAppBar(
        title = {
            Text(
                text = "Top Stories",
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
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        text = {
            Column {
                IconAndTextDrawerItem(
                    text = "Active",
                    icon = Icons.Default.Moving,
                    onClick = { onClickSortType(SortType.Active) },
                )
                IconAndTextDrawerItem(
                    text = "Hot",
                    icon = Icons.Default.LocalFireDepartment,
                    onClick = { onClickSortType(SortType.Hot) },
                )
                IconAndTextDrawerItem(
                    text = "New",
                    icon = Icons.Default.BrightnessLow,
                    onClick = { onClickSortType(SortType.New) },
                )
                IconAndTextDrawerItem(
                    text = "Top",
                    icon = Icons.Default.BarChart,
                    onClick = onClickSortTopOptions,
                    more = true,
                )
            }
        },
        buttons = {},
    )
}

@Composable
fun SortTopOptionsDialog(
    onDismissRequest: () -> Unit = {},
    onClickSortType: (SortType) -> Unit = {},
) {

    AlertDialog(
        onDismissRequest = onDismissRequest,
        text = {
            Column {
                IconAndTextDrawerItem(
                    text = "Top Day",
                    onClick = { onClickSortType(SortType.TopDay) },
                )
                IconAndTextDrawerItem(
                    text = "Top Week",
                    onClick = { onClickSortType(SortType.TopWeek) },
                )
                IconAndTextDrawerItem(
                    text = "Top Month",
                    onClick = { onClickSortType(SortType.TopMonth) },
                )
                IconAndTextDrawerItem(
                    text = "Top Year",
                    onClick = { onClickSortType(SortType.TopYear) },
                )
                IconAndTextDrawerItem(
                    text = "Top All Time",
                    onClick = { onClickSortType(SortType.TopAll) },
                )
            }
        },
        buttons = {},
    )
}

@Preview
@Composable
fun SortOptionsDialogPreview() {
    SortOptionsDialog()
}

@Composable
fun ListingTypeOptionsDialog(
    onDismissRequest: () -> Unit = {},
    onClickListingType: (ListingType) -> Unit = {},
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        text = {
            Column {
                IconAndTextDrawerItem(
                    text = "Subscribed",
                    icon = Icons.Default.Bookmarks,
                    onClick = { onClickListingType(ListingType.Subscribed) },
                )
                // TODO hide local for non-federated instances
                IconAndTextDrawerItem(
                    text = "Local",
                    icon = Icons.Default.LocationCity,
                    onClick = { onClickListingType(ListingType.Local) },
                )
                IconAndTextDrawerItem(
                    text = "All",
                    icon = Icons.Default.Public,
                    onClick = { onClickListingType(ListingType.All) },
                )
            }
        },
        buttons = {},
    )
}

@Preview
@Composable
fun ListingTypeOptionsDialogPreview() {
    ListingTypeOptionsDialog()
}

@Preview
@Composable
fun HomeHeaderPreview() {
    val scope = rememberCoroutineScope()
    val scaffoldState =
        rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
    HomeHeader(scope, scaffoldState)
}
