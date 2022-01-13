package com.jerboa.ui.components.inbox

import android.content.Context
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jerboa.db.Account
import com.jerboa.ui.components.post.InboxViewModel

fun inboxClickWrapper(
    inboxViewModel: InboxViewModel,
    account: Account?,
    navController: NavController,
    ctx: Context,
) {
    inboxViewModel.fetchReplies(
        account = account,
        clear = true,
        ctx = ctx,
    )
    navController.navigate(route = "inbox")
}

@Composable
fun InboxHeader(
    navController: NavController = rememberNavController(),
) {
    TopAppBar(
        title = {
            InboxHeaderTitle()
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        actions = {
            // TODO mark all as read, options
        }
    )
}

@Composable
fun InboxHeaderTitle() {
    Text(
        text = "Inbox",
        style = MaterialTheme.typography.subtitle1
    )
}
