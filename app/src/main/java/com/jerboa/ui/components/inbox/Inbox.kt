package com.jerboa.ui.components.inbox

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.runtime.*
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jerboa.UnreadOrAll
import com.jerboa.db.Account
import com.jerboa.ui.components.common.UnreadOrAllOptionsDialog
import com.jerboa.ui.theme.APP_BAR_ELEVATION
import com.jerboa.ui.theme.Muted

fun inboxClickWrapper(
    inboxViewModel: InboxViewModel,
    account: Account?,
    navController: NavController,
    ctx: Context,
) {
    account?.also {
        inboxViewModel.fetchReplies(
            account = account,
            clear = true,
            ctx = ctx,
        )
        inboxViewModel.fetchPersonMentions(
            account = account,
            clear = true,
            ctx = ctx,
        )
        inboxViewModel.fetchPrivateMessages(
            account = account,
            clear = true,
            ctx = ctx,
        )
        navController.navigate(route = "inbox")
    }
}

@Composable
fun InboxHeader(
    navController: NavController = rememberNavController(),
    selectedUnreadOrAll: UnreadOrAll,
    onClickUnreadOrAll: (UnreadOrAll) -> Unit,
    onClickMarkAllAsRead: () -> Unit,
    unreadCount: Int? = null,
) {
    var showUnreadOrAllOptions by remember { mutableStateOf(false) }

    if (showUnreadOrAllOptions) {
        UnreadOrAllOptionsDialog(
            selectedUnreadOrAll = selectedUnreadOrAll,
            onDismissRequest = { showUnreadOrAllOptions = false },
            onClickUnreadOrAll = {
                showUnreadOrAllOptions = false
                onClickUnreadOrAll(it)
            }
        )
    }

    TopAppBar(
        title = {
            InboxHeaderTitle(
                unreadCount = unreadCount,
                selectedUnreadOrAll = selectedUnreadOrAll,
            )
        },
        elevation = APP_BAR_ELEVATION,
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        actions = {
            IconButton(onClick = {
                showUnreadOrAllOptions = !showUnreadOrAllOptions
            }) {
                Icon(
                    Icons.Default.FilterList,
                    contentDescription = "TODO",
                    tint = MaterialTheme.colors.onSurface
                )
            }
            IconButton(onClick = onClickMarkAllAsRead) {
                Icon(
                    Icons.Default.DoneAll,
                    contentDescription = "TODO",
                    tint = MaterialTheme.colors.onSurface
                )
            }
        }
    )
}

@Composable
fun InboxHeaderTitle(selectedUnreadOrAll: UnreadOrAll, unreadCount: Int? = null) {
    var title = "Inbox"
    if (unreadCount != null && unreadCount > 0) {
        title = "$title ($unreadCount)"
    }
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.subtitle1,
            color = MaterialTheme.colors.onSurface,
        )
        Text(
            text = selectedUnreadOrAll.toString(),
            style = MaterialTheme.typography.body1,
            color = Muted,
        )
    }
}
