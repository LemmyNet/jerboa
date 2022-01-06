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
import com.jerboa.api.API
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

    Column {
        IconAndTextDrawerItem(
            text = "Add Account",
            onClick = { navController.navigate(route = "login") },
            icon = Icons.Default.Add,
        )
        accounts?.forEach {
            IconAndTextDrawerItem(
                text = "Switch to ${it.name}",
                onClick = {onSwitchAccountClick(it)},
                icon = Icons.Default.Login,
            )
        }
        accounts?.let {
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
            account?.let { Text(text = it.name, color = Color.White) }
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
    icon: ImageVector = Icons.Filled.Menu,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "TODO",
            modifier = Modifier
                .padding(12.dp)
                .size(24.dp)
        )
        Text(text = text, style = MaterialTheme.typography.subtitle1)
    }
}

@Preview
@Composable
fun IconAndTextDrawerItemPreview() {
    IconAndTextDrawerItem(text = "A test item", onClick = {})
}

@Composable
fun HomeHeader(
    scope: CoroutineScope,
    scaffoldState: ScaffoldState
) {
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
    )
}

@Preview
@Composable
fun HomeHeaderPreview() {
    val scope = rememberCoroutineScope()
    val scaffoldState =
        rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
    HomeHeader(scope, scaffoldState)
}
